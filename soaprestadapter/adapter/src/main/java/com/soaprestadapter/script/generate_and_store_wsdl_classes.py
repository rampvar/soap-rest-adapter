import os
import subprocess
import psycopg2
from datetime import datetime

# Configuration
WSDL_URLS = [
    "http://example.com/service1?wsdl",
    "http://example.com/service2?wsdl"
]
OUTPUT_DIR = "generated_classes"
DB_CONFIG = {
    "dbname": "your_db",
    "user": "your_user",
    "password": "your_password",
    "host": "localhost",
    "port": 5432
}

# Ensure output directory exists
os.makedirs(OUTPUT_DIR, exist_ok=True)

# Connect to the database
conn = psycopg2.connect(**DB_CONFIG)
cursor = conn.cursor()

# Create table if not exists
cursor.execute("""
CREATE TABLE IF NOT EXISTS wsdl_classes (
    id SERIAL PRIMARY KEY,
    wsdl_url TEXT,
    class_name TEXT,
    file_type VARCHAR(10),
    file_data BYTEA,
    generated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
)
""")
conn.commit()

# Process each WSDL
for wsdl_url in WSDL_URLS:
    package_name = "generated" + str(abs(hash(wsdl_url)))  # unique package
    wsdl_output_dir = os.path.join(OUTPUT_DIR, package_name)
    os.makedirs(wsdl_output_dir, exist_ok=True)

    # Run wsimport
    subprocess.run([
        "wsimport", "-keep", "-d", wsdl_output_dir, "-p", package_name, wsdl_url
    ], check=True)

    # Compile .java files
    for root, _, files in os.walk(wsdl_output_dir):
        for file in files:
            if file.endswith(".java"):
                java_file_path = os.path.join(root, file)
                subprocess.run(["javac", java_file_path], check=True)

    # Store .class files in DB as BLOBs
    for root, _, files in os.walk(wsdl_output_dir):
        for file in files:
            if file.endswith(".class"):
                class_file_path = os.path.join(root, file)
                with open(class_file_path, "rb") as f:
                    class_data = f.read()
                cursor.execute("""
                    INSERT INTO wsdl_classes (wsdl_url, class_name, file_type, file_data, generated_at)
                    VALUES (%s, %s, %s, %s, %s)
                """, (wsdl_url, file, "class", class_data, datetime.now()))
                conn.commit()

# Cleanup
cursor.close()
conn.close()
print("Java classes generated and stored successfully.")
