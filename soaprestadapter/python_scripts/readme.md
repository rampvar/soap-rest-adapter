# 🧾 COBOL Copybook to Structured Payload Generator

This Python script processes **COBOL copybooks** and **COBOL program files**, extracts request/response structures from the **LINKAGE SECTION**, parses them, and outputs:

- JSON structured payloads for REST APIs
- Flattened group-formatted copybook content
- Optional filtering based on sample XML
- Database inserts for structured and raw data

## 📂 Project Structure

```
.
├── config.py
├── payload_generator.py                 # (Your provided script)
├── copybooks/              # Folder containing `.cpy` files
├── xml_samples/            # Folder containing XML samples (optional)
├── programs.csv            # Input file with COBOL program paths and operation names
└── output.csv              # Final output with structured payloads
```

## 🛠 Features

- ✅ Parses nested and `COPY`-included copybooks
- ✅ Converts COBOL fields to camelCase
- ✅ Detects and extracts `REQUEST` and `RESPONSE` copybooks from LINKAGE SECTION
- ✅ Supports filtering fields using XML sample structure
- ✅ Inserts records into SQL-compatible database using SQLAlchemy
- ✅ Supports raw copybook storage with group-level start/end tags

## 📄 Sample `programs.csv`

```csv
ProgramName,OperationName
/path/to/MYPROGRAM.cbl,TRACK_ORDER
```

## ⚙️ Configuration (`config.py`)

Define these variables in `config.py`:

```python
input_csv_file = 'programs.csv'
output_csv = 'output.csv'
copybook_folder = 'copybooks'
sample_xml_folder = 'xml_samples'

enable_sample_xml_filtering = True
enable_database_insert = True
db_type = 'postgres'

db_config = {
    'postgres': {
        'url': 'postgresql://username:password@localhost:5432/your_db'
    },
    ...
}
```

## 🏗 Database Tables

### 1. `COBOL_FIXED_LENGTH_ATTRIBUTES`

| Column Name       | Type    | Description                  |
|------------------|---------|------------------------------|
| ProgramName       | String  | COBOL source file name       |
| OperationName     | String  | Operation name               |
| Request_payload1  | Text    | Basic metadata payload       |
| Request_payload2  | Text    | Full request structure       |

### 2. `tbl_response_copybook_data`

| Column Name        | Type   | Description                      |
|--------------------|--------|----------------------------------|
| id                 | Auto   | Primary Key                      |
| operation_name     | String | Operation name                   |
| request_header     | Text   | Request group structure (text)   |
| response_attributes| Text   | Response group structure (text)  |

## 🚀 How to Run

```bash
python payload_generator.py
```

## 🧪 Output

- `output.csv` — Structured payloads
- Inserts into your configured database (optional)
- Group-formatted copybook text stored in DB (`tbl_response_copybook_data`)

## 📝 Notes

- COBOL `COPY` statements are recursively resolved
- Groups (without `PIC`) are wrapped with `<GROUPNAME>_starts` and `<GROUPNAME>_ends`
- If `enable_sample_xml_filtering` is True, only fields appearing in corresponding `xml_samples/{OperationName}.xml` will be included in payload
- Fields are converted to camelCase format for consistency

## 📦 Requirements

Install dependencies:

```bash
pip install sqlalchemy
```

> Add `psycopg2` or `mysqlclient` based on the database type you're using.
