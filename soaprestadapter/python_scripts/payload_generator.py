import os
import re
import csv
import json
import xml.etree.ElementTree as ET
from typing import List, Dict

from sqlalchemy import create_engine, Column, String, Text
from sqlalchemy.ext.declarative import declarative_base
from sqlalchemy.orm import sessionmaker

from config import *

# ========== UTILITY FUNCTIONS ==========

def to_camel_case(fieldname: str) -> str:
    parts = fieldname.lower().split('-')
    return parts[0] + ''.join(word.capitalize() for word in parts[1:])


def extract_field_names_from_xml(xml_path: str) -> List[str]:
    def recurse_tags(element, prefix=""):
        tag_name = element.tag.split('}')[-1]
        current = f"{prefix}.{tag_name}" if prefix else tag_name
        children = list(element)
        return [current] if not children else sum([recurse_tags(c, current) for c in children], [])
    try:
        tree = ET.parse(xml_path)
        root = tree.getroot()
        flat_fields = recurse_tags(root)
        return [to_camel_case(os.path.basename(f)) for f in flat_fields]
    except Exception as e:
        print(f"⚠️  Failed to parse XML: {xml_path} - {e}")
        return []


# ========== COPYBOOK PARSER ==========

def parse_copybook(copybook_path: str, copybook_folder: str) -> List[Dict]:
    fields = []
    stack = []

    with open(copybook_path, 'r') as file:
        for line in file:
            # Handle COPY statements recursively
            copy_match = re.match(
                r"^\s*COPY\s+(?:'([^']+)'|([A-Z0-9\-\.]+))\s*(?:\.)?",
                line,
                re.IGNORECASE
            )
            if copy_match:
                nested = copy_match.group(1) or copy_match.group(2)
                if not nested.lower().endswith('.cpy'):
                    nested += '.cpy'
                nested_path = os.path.join(copybook_folder, nested)
                if os.path.exists(nested_path):
                    fields.extend(parse_copybook(nested_path, copybook_folder))
                continue

            # Parse COBOL fields with levels
            match = re.match(
                r'^\s*(\d+)\s+([\w-]+)(?:\s+PIC\s+([X9])\((\d+)\))?(?:\s+OCCURS\s+(\d+))?',
                line,
                re.IGNORECASE
            )
            if not match:
                continue

            level = int(match.group(1))
            name = match.group(2)
            pic_type = match.group(3)
            length = match.group(4)
            occurs = match.group(5)

            node = {
                'level': level,
                'fieldname': name,
                'children': []
            }
            if pic_type and length:
                node.update({
                    'pic_type': pic_type.upper(),
                    'length': int(length)
                })
            if occurs:
                node['occurs'] = int(occurs)

            # Maintain hierarchy using stack
            while stack and level <= stack[-1]['level']:
                stack.pop()

            if stack:
                stack[-1]['children'].append(node)
            else:
                fields.append(node)

            stack.append(node)

    return fields


# ========== STRUCTURED PAYLOAD GENERATOR ==========

def generate_structured_payload(fields: List[Dict], allowed: List[str] = None) -> Dict:
    def build_node(field):
        name = to_camel_case(field['fieldname'])
        if allowed and name not in allowed:
            return None

        if field['children']:
            children = {}
            for child in field['children']:
                child_node = build_node(child)
                if child_node:
                    children.update(child_node)
            return {name: children}

        if 'length' in field:
            base_type = 'int' if field.get('pic_type') == '9' else 'string'
            datatype = 'array' if 'occurs' in field else base_type

            obj = {
                'datatype': datatype,
                'length': field['length']
            }
            if 'occurs' in field:
                obj['occurrence'] = field['occurs']

            return {name: obj}
        else:
            # It's a group field with no PIC/length
            return {name: {}}


    result = {}
    for f in fields:
        node = build_node(f)
        if node:
            result.update(node)
    return result


# ========== COPYBOOK DETECTOR ==========

def extract_copybook_references_from_linkage(path: str) -> List[str]:
    cbk = []
    with open(path, 'r') as file:
        in_linkage = False
        for line in file:
            if re.match(r'\s*LINKAGE\s+SECTION', line, re.IGNORECASE):
                in_linkage = True
            elif re.match(r'\s*PROCEDURE\s+DIVISION', line, re.IGNORECASE):
                in_linkage = False
            if in_linkage:
                m = re.search(r'\s*COPY\s+(?:\'([^\']+)\'|([A-Z0-9\-\.]+))', line, re.IGNORECASE)
                if m:
                    cbk.append(m.group(1) or m.group(2))
    return cbk


def read_programs_from_csv(file_path: str) -> List[Dict[str, str]]:
    with open(file_path, 'r') as file:
        return [dict(row) for row in csv.DictReader(file)]


# ========== DATABASE ==========

Base = declarative_base()

class PayloadRecord(Base):
    __tablename__ = 'COBOL_FIXED_LENGTH_ATTRIBUTES'
    program_name = Column('ProgramName', String(256), primary_key=True)
    operation_name = Column('OperationName', String(256))
    rest_payload = Column('Request_payload1', Text)
    rest_payload2 = Column('Request_payload2', Text)

def get_engine():
    if db_type not in db_config:
        raise ValueError(f"Unsupported DB type: {db_type}")
    url = db_config[db_type]['url']
    if db_type == 'h2':
        raise NotImplementedError("H2 database integration not supported natively in SQLAlchemy")
    return create_engine(url)


# ========== MAIN DRIVER ==========

def process_and_generate():
    rows = []
    programs = read_programs_from_csv(input_csv_file)

    for entry in programs:
        cbl_path = entry['ProgramName']
        op_name = entry['OperationName']
        base_name = os.path.basename(cbl_path)

        if not os.path.exists(cbl_path):
            print(f"⚠️  Missing: {cbl_path}")
            continue

        cbk_refs = extract_copybook_references_from_linkage(cbl_path)
        fields = []
        for cb in cbk_refs:
            filename = cb.strip("'.\"")  # clean quotes or periods
            if not filename.lower().endswith('.cpy'):
                filename += '.cpy'
            path = os.path.join(copybook_folder, filename)
            if os.path.exists(path):
                fields.extend(parse_copybook(path, copybook_folder))
            else:
                print(f"⚠️  Copybook not found: {path}")

        # Optional filtering using sample XML
        allowed_fields = None
        if enable_sample_xml_filtering:
            xml_path = os.path.join(sample_xml_folder, op_name + '.xml')
            if os.path.exists(xml_path):
                allowed_fields = extract_field_names_from_xml(xml_path)

        # Generate structured payload
        payload = generate_structured_payload(fields, allowed_fields)
        payload_str = json.dumps(payload, indent=2)
        payload2_str = json.dumps({"operationName": op_name, "programName": base_name})

        rows.append({
            'ProgramName': base_name,
            'OperationName': op_name,
            'Request_payload1': payload2_str,
            'Request_payload2': payload_str
        })

    # CSV Output
    with open(output_csv, 'w', newline='') as file:
        writer = csv.DictWriter(file, fieldnames=['ProgramName', 'OperationName', 'Request_payload1', 'Request_payload2'])
        writer.writeheader()
        writer.writerows(rows)

    print(f"\n✅ Output saved: {output_csv}")

    # Database Output
    if enable_database_insert and db_type != 'h2':
        engine = get_engine()
        Base.metadata.create_all(engine)
        Session = sessionmaker(bind=engine)
        session = Session()
        for r in rows:
            record = PayloadRecord(
                program_name=r['ProgramName'],
                operation_name=r['OperationName'],
                rest_payload=r['Request_payload1'],
                rest_payload2=r['Request_payload2']
            )
            session.merge(record)
        session.commit()
        print(f"📦 Data committed to database ({db_type})")


# ========== RUN ==========
if __name__ == '__main__':
    process_and_generate()
