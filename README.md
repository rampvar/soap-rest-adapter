# Table of Contents

- [Purpose of the Project](#purpose-of-the-project)
- [Prerequisites](#prerequisites)
- [Project Structure](#project-structure)
- [Module Structure](#module-structure)
- [adapter](#adapter-)
- [application](#application-)
- [connector](#connector-)
- [converter](#converter-)
- [database](#database)
- [handler](#handler)
- [oidc](#oidc)
- [python_scripts](#python_scripts-)
- [How to Build](#how-to-build)




# soap-rest-adapter
The SOAP-REST Adapter is a modular integration framework built on Apache Camel and CXF,
designed to handle SOAP requests based on WSDL definitions. It routes and transforms incoming SOAP messages
to RESTful calls and delegates response generation to downstream implementations such as AMT,
Bluage, or other pluggable service modules.
# Purpose of the Project:
This project receives a SOAP XML request and converts it into a REST request (i.e., JSON format).

# Prerequisites
- Java  21
- Python 3.x
- Maven 3.8+
- DataBase Compatibility
    - MySQL database 
    - h2
    - oracle
    - postgres
    - sqlite
- Git
- Postman
- SoapUI

# Project Structure

soaprestadapter/
│
├── adapter/
├── application/
├── connector/
├── converter/
├── database/
├── handler/
├── oidc/
├── pythonscripts/
└── README.md


##  Module Structure

| Module         | Description |
|----------------|-------------|
| `adapter`      | Core SOAP/REST adapter logic, dynamic class loading |
| `application`  | Spring Boot entry point, routes, service orchestration |
| `checks`       | Health checks, startup validations, dependency verifications |
| `connector`    | Interfaces for external systems (e.g., COBOL, legacy systems, downstream APIs) |
| `converter`    | Converts between JSON, XML, SOAP formats; handles runtime marshalling/unmarshalling |
| `database`     | Manages schema scripts, JDBC, and entity persistence |
| `handler`      | Exception handlers, global error handling, fallback logic |
| `oidc`         | OAuth2/OpenID Connect integration for JWT validation |
| `pythonscripts`| Optional integration with Python scripts via subprocess or REST |

# adapter :
* This module takes wsdl and xsd URLs as parameter and generate .class files which will then converted to blob and storing in databse (tbl_generated_wsdl_classes ,cobol_fixed_length_attributes)
* Sample Curl command to hit the Rest endpoint
curl --location 'http://localhost:8080/generate/from-urls' \
--header 'Content-Type: application/json' \
--data '[
{
"wsdlUrl": "https://raw.githubusercontent.com/raghavM16/test/main/service.wsdl",
"xsdUrls": ["https://raw.githubusercontent.com/raghavM16/test/main/schema.xsd"]
  },
{
"wsdlUrl": "https://raw.githubusercontent.com/raghavM16/test/main/serviceWorking.wsdl",
"xsdUrls": []
}
]'
# Sample WSDL Used for Class Generation
<details>
<summary>📄 Click to view WSDL (TshirtService.wsdl)</summary>

```xml
<?xml version="1.0" encoding="UTF-8"?>
<wsdl:definitions xmlns:wsdl="http://schemas.xmlsoap.org/wsdl/"
                  xmlns:ms="http://mulesoft.org/tshirt-service"
                  xmlns:soap="http://schemas.xmlsoap.org/wsdl/soap/"
                  name="TshirtService"
                  targetNamespace="http://mulesoft.org/tshirt-service">

    <!-- full WSDL content here (as you've shared) -->
</wsdl:definitions>
```
</details> 

* Handles dynamic class loading at application startup by fetching class files stored in the database (as BLOBs) and loading them into the module’s classpath. These dynamically loaded classes become accessible to other modules using a custom class loader and registry mechanism.
* Class loading flow:
  BlobLoaderInitializer → BlobClassLoaderService
* At bootstrap, all class files are fetched from the database.
* They are loaded into the classpath using a custom BlobClassLoader.
* Accessing loaded classes from other modules:
   BlobClassRegistry → DynamicInvoker
* BlobClassRegistry stores references to all loaded classes.
* DynamicInvoker provides utility methods to fetch and use these classes via reflection.This mechanism allows runtime flexibility where generated WSDL classes or other external artifacts can be loaded without compile-time dependencies.

# application :

* SoapAdapterRoute has the entry point for all cxf endpoints.

Configuration:
The application.yml file inside application module defines configuration for the SOAP-REST Adapter, including

1. Camel CXF Endpoints
camel:
  cxf:
    inventory: /inventory
    hello: /hello

These paths expose CXF SOAP endpoints via Apache Camel. Each key (e.g., inventory, hello) maps to a specific SOAP service that will be accessible under the given URI. For example:
http://<host>:<port>/services/inventory will expose the inventory SOAP service.
http://<host>:<port>/services/hello will expose the hello SOAP service.

2. Spring Profiles
spring:
  profiles:
    active: mysql
This activates the mysql Spring profile, which allows you to maintain environment-specific configurations (e.g., database URLs, credentials, etc.) in application-mysql.yml.

3. Adapter Connector Selection
app:
  connector: AMT
This configures the adapter to use the AMT connector by default. This setting determines which downstream service implementation the SOAP requests will be routed to (e.g., AMT or BLUAGE). This value can be dynamically used to route calls based on business logic.

4. Service Endpoints
services:
  endpoints:
    AMT:
      TrackOrder: http://localhost:8080/amt/track_order
      OrderTshirt: http://localhost:8080/amt/order_tshirt
    BLUAGE:
      TrackOrder: http://localhost:8080/bluage/track_order
      OrderTshirt: http://localhost:8080/bluage/order_tshirt
This section defines REST endpoint URLs for different service providers (AMT and BLUAGE) and operations (e.g., TrackOrder, OrderTshirt). The adapter dynamically selects the appropriate endpoint based on the operation and the configured connector.


# connector :
* This module receives the rest payload in json format. Based on key mentioned in application.yml file, rest request  is dispatches to different implementations.
* In this application AMT and Bluage implementations has covered.
* This modules receives the input from COBOL_FIXED_LENGTH_ATTRIBUTES table.Request_payload1 is having operationName and programName.Request_payload2 is having request attributes along with datatype and length.
* Connector factory class decides the implementation to invoke at runtime based on key passed in application yml file.
* Requestdispatcher class uses connector factory and invokes execute method.
  Note: JsonData is formed using input soap xml and Request_payload2 from COBOL_FIXED_LENGTH_ATTRIBUTES table. input soap xml and Request_payload2 attributes are compared using datatype and length. If attribute length less than mentioned in payload2, Space is padded to right for string datatype.
  AmtServiceImpl and BluageServiceImpl has generatePayload method which generates input payload for these two implementations as per their contract.
* Amt and Bluage service endpoints are mentioned in application.yml file
* AmtController and BluageController classes has handleRequests for different operations.
* RestClientService class has process method which invokes rest call using restTemplate web client.
* Json response for particular operation generated in connector module is passed to handler module for further processing.

# converter :
* This module converts a JSON string into a SOAP-compatible XML response.
* First deserializing the JSON into a Java object of type T using a reusable Jackson ObjectMapper
* Next marshalling that object into a formatted XML string with the JAXB Marshaller.
* The result is a well-structured, human-readable XML response that conforms to SOAP standards.
* It leverages Jackson Databind for JSON processing, Jakarta JAXB API for XML marshalling,
  and Lombok for streamlined logging and code simplification.

# database :
* Responsible for managing the application's connection to the database. It supports configuration for multiple databases such as H2, MySQL, Oracle, PostgreSQL, and SQLite.
* Each database type has its specific configuration defined in separate application-<db>.yml files. 
* To activate a particular database, you must update the profile as  per db name main application.yml file located in the application module, which serves as the entry point of the entire system.
* Dependency need to be added for respective database:

# Supported Databases

| Database   | Driver Dependency                  | Profile Name | Notes                                      |
|------------|------------------------------------|--------------|--------------------------------------------|
| MySQL      | `mysql:mysql-connector-java`       | `mysql`      | Default profile for production             |
| H2         | `com.h2database:h2`                | `h2`         | In-memory database for testing             |
| Oracle     | `com.oracle.database.jdbc:ojdbc8`  | `oracle`     | May require manual download due to license |
| PostgreSQL | `org.postgresql:postgresql`        | `postgres`   | Easy integration with Spring Boot          |
| SQLite     | `org.xerial:sqlite-jdbc`           | `sqlite`     | Lightweight, file-based storage            |



# handler :
* This is Response handler which will transform the response body received from endpoint.
* Based on below types, handler will be invoked through factory.
  AMT-RESPONSE
  BLUEAGE-RESPONSE
  CUSTOM-RESPONSE
* Handler methods are invoked from respective connector implementation class.
* AMT Response handler will interact with DB to fetch response copybook data based on operation name. The table details are given below.
  Table name: tbl_response_copybook_data
* The converted Json String is passed to respective converter service which will convert rest response to soap response.
* Overall the main intention of Handler module is to add capability to convert rest response to specific required format so as to form soap response further.

#  OIDC :
*  User authorization Mechanism is handled through two configurable strategies:
  * User-Role-Group Mapping
  * AWS IAM Role-based Authorization
*  Entitlement Factory Class will decide how we are authorization the user based on the configuration made in application.yml file.
*  AWS IAM role based authorization is done either in local or cloud environment.
*  We have interceptor layer which will intercept input request and validate user based on userId for user-role-group authorization.
*  AWS Iam authorization role is done based on username and action.
*  Interceptor class will get jwt token from headers and store that token in soap body for future use, will use this jwt token for calling 3rd party services like amt/bluage or other custom endpoints
     if user is not entitled then throw error message as User Unauthorized.

    # Authorization Server Project

*  Input request will redirect to this server to generate jwt token which will be used in calling Rest endpoin of AMT/AWS blue age/Custom API.
*  Generated jwt will be set in input xml header fields as key value pair "jwt_token" as key and value as generated token.
*  Gateway endpoint will be exposed in port defined in application.yml.


# python_scripts :
  Refer Readme file in this module .
  https://github.com/rampvar/soap-rest-adapter/blob/main/soaprestadapter/python_scripts/readme.md

# Process to adapt new WSDL
* If any new service need to be add , then required steps are:
configure url like below those are configured:
  curl --location 'http://localhost:8080/generate/from-urls' \
  --header 'Content-Type: application/json' \
  --data '[
  {
  "wsdlUrl": "https://raw.githubusercontent.com/raghavM16/test/main/service.wsdl",
  "xsdUrls": ["https://raw.githubusercontent.com/raghavM16/test/main/schema.xsd"]
  },
  {
  "wsdlUrl": "https://raw.githubusercontent.com/raghavM16/test/main/serviceWorking.wsdl",
  "xsdUrls": []
  }
  ]'
* After that above mentioned flow be executed .




#  How to Build

```bash
# Build all modules
 mvn clean install
