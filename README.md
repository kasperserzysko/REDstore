<h1 align="center">REDstore </h1>

<h4 align="center">A Backend REST application made for CD Project RED </h4>


## Technologies
* SpringBoot 
* MySQL
* Spring security
* JWT
* Spring validation
* Spring email
* Lombok
* Cache

## Documentation:
  Application has 2 different ports for client and admin:
  * Endpoints documentiation with SWAGGER with running app:
    - for Client app: http://localhost:8081/swagger-ui/index.html#/
    - for Admin app: http://localhost:8080/swagger-ui/index.html#/
  * Or here:
    - for Client app: <a href="https://drive.google.com/file/d/19BUKR1YdD5RtjD_XQbpDV8gtjZimL-zO/view?usp=share_link">client_documentation</a>
    - for Admin app: <a href="https://drive.google.com/file/d/1Smd2KxDuzZANx8o0O-aYzlHEbZt82Bsm/view?usp=share_link">admin_documentation</a>
    
    
## How To Use

Clone this reposity, then change in:
  - web/client_app/src/main/resources/application.properties for Your MySQL database,
  - web/admin_app/src/main/resources/application.properties for Your MySQL database
  - email_service/src/main/java/com/kasperserzysko/email_service/configuration/EmailConfiguration.java EMAIL_USERNAME and EMAIL_PASSWORD (gmail application password) for Your email credentials.
  - tools/src/main/java/com/kasperserzysko/tools/FileService.java FOLDER_PATH for desired destination

