import os
import re
import csv
import json
import xml.etree.ElementTree as ET
from typing import List, Dict, Tuple

from sqlalchemy import create_engine, Column, String, Text, Integer
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

            node = {'level': level, 'fieldname': name, 'children': []}
            if pic_type and length:
                node.update({'pic_type': pic_type.upper(), 'length': int(length)})
            if occurs:
                node['occurs'] = int(occurs)

            while stack and level <= stack[-1]['level']:
                stack.pop()

            if stack:
                stack[-1]['children'].append(node)
            else:
                fields.append(node)

            stack.append(node)

    return fields

# ========== FORMAT COPYBOOK TEXT ==========

def format_copybook_with_groups(copybook_path: str, copybook_folder: str) -> str:
    output = []
    group_stack = []

    if not os.path.exists(copybook_path):
        return ""

    with open(copybook_path, 'r') as f:
        lines = f.readlines()

    for line in lines:
        stripped = line.strip()

        # Recursively include COPY content
        copy_match = re.match(r'^\s*COPY\s+(?:\'([^\']+)\'|([A-Z0-9\-\.]+))', stripped, re.IGNORECASE)
        if copy_match:
            copy_file = copy_match.group(1) or copy_match.group(2)
            if not copy_file.lower().endswith('.cpy'):
                copy_file += '.cpy'
            nested_path = os.path.join(copybook_folder, copy_file)
            nested_text = format_copybook_with_groups(nested_path, copybook_folder)
            output.append(nested_text)
            continue

        # Match lines like: 01 TRACK-ORDER. or 05 EMAIL PIC X(100).
        match = re.match(r'^\s*(\d+)\s+([\w-]+)(.*)', stripped)
        if not match:
            continue

        level = int(match.group(1))
        name = match.group(2)
        rest = match.group(3)

        # Close any groups with higher or same level
        while group_stack and level <= group_stack[-1][0]:
            _, grp_name = group_stack.pop()
            output.append(f"{' ' * 4}{grp_name}_ends")

        # If no PIC, it's a group
        if 'PIC' not in rest.upper():
            group_stack.append((level, name))
            output.append(f"{' ' * 4}{name}_starts")
        else:
            output.append(line.rstrip())

    # Close remaining groups
    while group_stack:
        _, grp_name = group_stack.pop()
        output.append(f"{' ' * 4}{grp_name}_ends")

    return "\n".join(output)

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
            obj = {'datatype': datatype, 'length': field['length']}
            if 'occurs' in field:
                obj['occurrence'] = field['occurs']
            return {name: obj}
        else:
            return {name: {}}
    result = {}
    for f in fields:
        node = build_node(f)
        if node:
            result.update(node)
    return result

# ========== LINKAGE COPYBOOK DETECTOR ==========

def extract_copybook_references_from_linkage(path: str) -> Tuple[List[str], List[str]]:
    request_copies = []
    response_copies = []
    current_group = None

    with open(path, 'r') as file:
        in_linkage = False
        for line in file:
            if re.match(r'\s*LINKAGE\s+SECTION', line, re.IGNORECASE):
                in_linkage = True
            elif re.match(r'\s*PROCEDURE\s+DIVISION', line, re.IGNORECASE):
                in_linkage = False

            if in_linkage:
                group_match = re.match(r'\s*\d+\s+([\w-]+)\.', line)
                if group_match:
                    current_group = group_match.group(1).strip().upper()
                copy_match = re.search(r'\s*COPY\s+(?:\'([^\']+)\'|([A-Z0-9\-\.]+))', line, re.IGNORECASE)
                if copy_match and current_group:
                    copybook_name = copy_match.group(1) or copy_match.group(2)
                    if 'REQUEST' in current_group:
                        request_copies.append(copybook_name)
                    elif 'RESPONSE' in current_group:
                        response_copies.append(copybook_name)
    return request_copies, response_copies

def read_programs_from_csv(file_path: str) -> List[Dict[str, str]]:
    with open(file_path, 'r') as file:
        return [dict(row) for row in csv.DictReader(file)]

# ========== DATABASE MODELS ==========

Base = declarative_base()

class PayloadRecord(Base):
    __tablename__ = 'COBOL_FIXED_LENGTH_ATTRIBUTES'
    program_name = Column('ProgramName', String(256), primary_key=True)
    operation_name = Column('OperationName', String(256))
    rest_payload = Column('Request_payload1', Text)
    rest_payload2 = Column('Request_payload2', Text)

class CopybookRawRecord(Base):
    __tablename__ = 'tbl_response_copybook_data'
    id = Column(Integer, primary_key=True, autoincrement=True)
    operation_name = Column('operation_name', String(255))
    request_header = Column('request_header', Text)
    response_attributes = Column('response_attributes', Text)

def get_engine():
    if db_type not in db_config:
        raise ValueError(f"Unsupported DB type: {db_type}")
    url = db_config[db_type]['url']
    if db_type == 'h2':
        raise NotImplementedError("H2 database integration not supported")
    return create_engine(url)

def store_response_copybook_data(session, operation_name: str, response_attributes: str, request_header: str):
    record = CopybookRawRecord(
        operation_name=operation_name,
        request_header=request_header,
        response_attributes=response_attributes
    )
    session.add(record)
    print(f"📥 Prepared insert for: {operation_name}")

# ========== MAIN DRIVER ==========

def process_and_generate():
    rows = []
    programs = read_programs_from_csv(input_csv_file)
    engine = get_engine()
    Base.metadata.create_all(engine)
    Session = sessionmaker(bind=engine)
    session = Session()

    for entry in programs:
        cbl_path = entry['ProgramName']
        op_name = entry['OperationName']
        base_name = os.path.basename(cbl_path)

        if not os.path.exists(cbl_path):
            print(f"⚠️  Missing: {cbl_path}")
            continue

        request_copies, response_copies = extract_copybook_references_from_linkage(cbl_path)

        request_fields = []
        request_text_blocks = []
        for cb in request_copies:
            filename = cb.strip("'.\"")
            if not filename.lower().endswith('.cpy'):
                filename += '.cpy'
            path = os.path.join(copybook_folder, filename)
            if os.path.exists(path):
                request_fields.extend(parse_copybook(path, copybook_folder))
                request_text_blocks.append(format_copybook_with_groups(path, copybook_folder))
            else:
                print(f"⚠️  Request copybook not found: {path}")
        request_header_str = "\n".join(request_text_blocks).strip()

        response_fields = []
        response_text_blocks = []
        for cb in response_copies:
            filename = cb.strip("'.\"")
            if not filename.lower().endswith('.cpy'):
                filename += '.cpy'
            path = os.path.join(copybook_folder, filename)
            if os.path.exists(path):
                response_fields.extend(parse_copybook(path, copybook_folder))
                response_text_blocks.append(format_copybook_with_groups(path, copybook_folder))
            else:
                print(f"⚠️  Response copybook not found: {path}")
        response_attr_str = "\n".join(response_text_blocks).strip()

        allowed_fields = None
        if enable_sample_xml_filtering:
            xml_path = os.path.join(sample_xml_folder, op_name + '.xml')
            if os.path.exists(xml_path):
                allowed_fields = extract_field_names_from_xml(xml_path)

        payload = generate_structured_payload(request_fields, allowed_fields)
        payload_str = json.dumps(payload, indent=2)
        payload2_str = json.dumps({"operationName": op_name, "programName": base_name})

        rows.append(PayloadRecord(
            program_name=base_name,
            operation_name=op_name,
            rest_payload=payload2_str,
            rest_payload2=payload_str
        ))

        store_response_copybook_data(session, op_name, response_attr_str, request_header_str)

    with open(output_csv, 'w', newline='') as file:
        writer = csv.DictWriter(file, fieldnames=['ProgramName', 'OperationName', 'Request_payload1', 'Request_payload2'])
        writer.writeheader()
        writer.writerows([{
            'ProgramName': r.program_name,
            'OperationName': r.operation_name,
            'Request_payload1': r.rest_payload,
            'Request_payload2': r.rest_payload2
        } for r in rows])

    print(f"\n✅ Output saved: {output_csv}")

    if enable_database_insert and db_type != 'h2':
        session.bulk_save_objects(rows)
        session.commit()
        print(f"📦 All records committed to database ({db_type})")

# ========== RUN ==========
if __name__ == '__main__':
    process_and_generate()
