============================================
WSDL/XSD to Java Class Generator & Storage
============================================

This script automates:
  - Downloading WSDL and XSD files.
  - Generating Java classes from WSDL using Apache CXF's wsdl2java.
  - Storing the generated .class files into a MySQL database table.

📌 REQUIREMENTS:
--------------------------------------------
1️⃣ Python Packages:
   - mysql-connector-python
     pip install mysql-connector-python

2️⃣ Java:
   - JDK must be installed.
   - Set JAVA_HOME correctly in the script or your system.
   - Example: JDK 21

3️⃣ Apache CXF:
   - Download and extract Apache CXF (tested with version 4.0.4).
   - The 'wsdl2java.bat' must be available under:
     C:\Program Files\Java\apache-cxf-4.0.4\bin

4️⃣ MySQL Database:
   - A MySQL server must be running.
   - The DB name, user, password, host and port are defined in the script.

📌 HOW TO USE:
--------------------------------------------
1️⃣ Update `WSDL_XSD_URLS` list in the script with your own WSDL and XSD URLs.
   - Example:
     [
       "https://yourdomain.com/service.wsdl",
       "https://yourdomain.com/schema.xsd"
     ]

   If a WSDL is immediately followed by an XSD in the list, it will treat them as linked.

2️⃣ Update `DB_CONFIG` in the script with your own MySQL credentials.

3️⃣ Ensure your JAVA_HOME and Apache CXF paths are set correctly.

   Example in script:
     env["JAVA_HOME"] = r"C:\Program Files\Java\jdk-21"
     env["PATH"] = rf"C:\Program Files\Java\jdk-21\bin;C:\Program Files\Java\apache-cxf-4.0.4\bin;{env['PATH']}"

4️⃣ Run the script:
     python your_script_name.py

5️⃣ The script will do:
     - Download the WSDL and any linked XSD files.
     - Run `wsdl2java` to generate Java source files.
     - Compile them to .class files.
     - Insert all .class files as a single blob in `tbl_generated_wsdl_classes`.

📌 OUTPUT:
--------------------------------------------
- Generated class files are stored under the `generated_classes` folder.
- The `tbl_generated_wsdl_classes` table stores:
   - Source WSDL URL
   - Binary .class data (combined)
   - Generation timestamp

