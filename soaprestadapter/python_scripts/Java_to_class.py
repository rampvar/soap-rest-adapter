import os
import subprocess
import mysql.connector
import urllib.request
from datetime import datetime
import re

# ---------------------------------------------------
# ✅ Config
# ---------------------------------------------------
WSDL_XSD_URLS = [
    # Example: wsdl + xsd pair
    "https://raw.githubusercontent.com/raghavM16/test/main/service.wsdl",
    "https://raw.githubusercontent.com/raghavM16/test/main/schema.xsd",

    # Example: standalone wsdl
    "https://raw.githubusercontent.com/raghavM16/test/main/serviceWorking.wsdl"
]

OUTPUT_DIR = "generated_classes"

DB_CONFIG = {
    "database": "mydb",
    "user": "root",
    "password": "Dongle@99f",
    "host": "localhost",
    "port": 3306
}

# ---------------------------------------------------
# ✅ Ensure output dir
# ---------------------------------------------------
os.makedirs(OUTPUT_DIR, exist_ok=True)

# ---------------------------------------------------
# ✅ DB Connection
# ---------------------------------------------------
conn = mysql.connector.connect(**DB_CONFIG)
cursor = conn.cursor()

cursor.execute("""
CREATE TABLE IF NOT EXISTS tbl_generated_wsdl_classes (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    wsdl_url VARCHAR(255) NOT NULL,
    class_data LONGBLOB NOT NULL,
    generated_at DATETIME NOT NULL
)
""")
conn.commit()

# ---------------------------------------------------
# ✅ Helpers
# ---------------------------------------------------
def is_wsdl(url):
    return url.lower().endswith(".wsdl")

def is_xsd(url):
    return url.lower().endswith(".xsd")

def download_file(url, local_path):
    urllib.request.urlretrieve(url, local_path)
    print(f"✅ Downloaded: {local_path}")
    return local_path

def download_wsdl_and_linked_xsd(wsdl_url, output_dir, next_xsd_url=None):
    wsdl_file = os.path.join(output_dir, "service.wsdl")
    download_file(wsdl_url, wsdl_file)

    # Download linked schemas in WSDL
    with open(wsdl_file, "r", encoding="utf-8") as f:
        wsdl_content = f.read()

    schema_refs = re.findall(r'schemaLocation="([^"]+\.xsd)"', wsdl_content)
    for schema_ref in schema_refs:
        if schema_ref.startswith("http"):
            schema_url = schema_ref
        else:
            schema_url = wsdl_url.rsplit("/", 1)[0] + "/" + schema_ref
        local_schema = os.path.join(output_dir, os.path.basename(schema_ref))
        download_file(schema_url, local_schema)

    # If user explicitly gave next xsd → also download it here
    if next_xsd_url:
        local_xsd = os.path.join(output_dir, os.path.basename(next_xsd_url))
        download_file(next_xsd_url, local_xsd)

    return wsdl_file

def process_wsdl(wsdl_url, output_dir, next_xsd_url=None):
    print(f"🚀 Processing WSDL: {wsdl_url}")
    wsdl_file = download_wsdl_and_linked_xsd(wsdl_url, output_dir, next_xsd_url)

    env = os.environ.copy()
    env["JAVA_HOME"] = r"C:\Program Files\Java\jdk-21"
    env["PATH"] = rf"C:\Program Files\Java\jdk-21\bin;C:\Program Files\Java\apache-cxf-4.0.4\bin;{env['PATH']}"

    subprocess.run([
        r"C:\Program Files\Java\apache-cxf-4.0.4\apache-cxf-4.0.4\bin\wsdl2java.bat",
        "-d", output_dir,
        "-compile",
        wsdl_file
    ], check=True, env=env)
    remove_java_files(output_dir)

    print(f"✅ WSDL processed: {wsdl_url}")



def store_class_files(source_url, output_dir):
    class_data_combined = bytearray()
    file_count = 0

    for root, _, files in os.walk(output_dir):
        for file in files:
            if file.endswith(".class"):
                with open(os.path.join(root, file), "rb") as f:
                    class_data_combined += f.read()
                file_count += 1

    if file_count > 0:
        cursor.execute("""
            INSERT INTO tbl_generated_wsdl_classes(
                wsdl_url, class_data, generated_at
            ) VALUES (%s, %s, %s)
        """, (source_url, class_data_combined, datetime.now()))
        conn.commit()
        print(f"✅ Stored {file_count} .class files as single entry for {source_url}")
    else:
        print(f"⚠️ No .class files found for {source_url}")

def remove_java_files(output_dir):
    removed = 0
    for root, _, files in os.walk(output_dir):
        for file in files:
            if file.endswith(".java"):
                try:
                    os.remove(os.path.join(root, file))
                    removed += 1
                except Exception as e:
                    print(f"⚠️ Could not delete {file}: {e}")


# ---------------------------------------------------
# ✅ Main loop
# ---------------------------------------------------
i = 0
while i < len(WSDL_XSD_URLS):
    url = WSDL_XSD_URLS[i]
    out_dir = os.path.join(OUTPUT_DIR, "generated_" + str(abs(hash(url))))
    os.makedirs(out_dir, exist_ok=True)

    if is_wsdl(url):
        next_xsd_url = None
        if (i + 1 < len(WSDL_XSD_URLS)) and is_xsd(WSDL_XSD_URLS[i + 1]):
            next_xsd_url = WSDL_XSD_URLS[i + 1]
            print(f"🔗 Treating {url} + {next_xsd_url} as one unit")
            i += 1  # skip the xsd in next loop
        process_wsdl(url, out_dir, next_xsd_url)
        store_class_files(url, out_dir)

    elif is_xsd(url):
        print(f"⚠️ Skipping standalone XSD: {url} (was not paired with WSDL)")

    else:
        print(f"❌ Unsupported: {url}")

    i += 1

# ✅ Done
cursor.close()
conn.close()
print("🎉 ALL WSDL Urls succesfully processed")
