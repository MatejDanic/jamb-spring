
## Starting the project

### Database

Using PostgresSQL GUI pgAdmin, create a database named "jamb" on the localhost server.
In the application.properties file, edit username and password of the database connection to match your local server credentials.

In the project directory, run command:

### `mvn spring-boot:run`

This command starts up the Spring Boot application by running the Main method located in Main.java.
This process initializes the database tables, their columns and constraints that are defined in the models folder.

The api should now be available via localhost:8080/ url.
