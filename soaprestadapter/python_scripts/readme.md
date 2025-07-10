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




🌐 WSDL/XSD to Java Class Generator & Storage

This script automates:

✅ Downloading WSDL and XSD files.

✅ Generating Java classes from WSDL using Apache CXF’s wsdl2java.

✅ Storing the generated .class files into a MySQL database table.


📌 REQUIREMENTS

1️⃣ Python Packages:

pip install mysql-connector-python

JDK must be installed.

Set JAVA_HOME correctly in the script or your system.

Example: JDK 21


3️⃣ Apache CXF:

Download and extract Apache CXF (tested with version 4.0.4).

The wsdl2java.bat must be available under:

C:\Program Files\Java\apache-cxf-4.0.4\bin


4️⃣ MySQL Database:

A MySQL server must be running.

The DB name, user, password, host, and port are defined in the script.


📌 HOW TO USE URL: [WSDL+XSD] and without XSD

✅ Using WSDL URL with a linked XSD

If your WSDL depends on an external XSD file, make sure to list the WSDL first and the XSD immediately after in the WSDL_XSD_URLS list.

WSDL_XSD_URLS =[

    "https://example.com/myService.wsdl",
    
    "https://example.com/schema1.xsd"
]

The script will detect that the .wsdl is followed by a .xsd and will download both, treating them as a single unit.

You can pair multiple WSDL+XSD sets in the same list:

WSDL_XSD_URLS = [

    "https://example.com/service1.wsdl",
    
    "https://example.com/schema1.xsd",
    
    "https://example.com/service2.wsdl",
    
    "https://example.com/schema2.xsd"
]


✅ Using WSDL without XSD

If your WSDL does not need an external XSD, just list the .wsdl by itself.

WSDL_XSD_URLS = [

    "https://example.com/service3.wsdl"
    
]


2️⃣ Update DB_CONFIG in the script with your own MySQL credentials.


3️⃣ Ensure your JAVA_HOME and Apache CXF paths are set correctly.

Example in script:

env["JAVA_HOME"] = r"C:\Program Files\Java\jdk-21"

env["PATH"] = rf"C:\Program Files\Java\jdk-21\bin;C:\Program Files\Java\apache-cxf-4.0.4\bin;{env['PATH']}"


4️⃣ Run the script:

python Java_to_class.py


📌 OUTPUT
Generated class files are stored under the generated_classes folder.

The tbl_generated_wsdl_classes table stores:

Source WSDL URL

Binary .class data 

Generation timestamp


