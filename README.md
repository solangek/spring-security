# a simple Spring security application

The application has 3 areas for the user to access:
- /admin for ADMIN roles
- /shared for ADMIN and USER roles
- /user for USER roles

User credentials are defined in the Configuration class.
ADMIN role: admin, password: password
USER role: user, password: password
ADMIN and USER role: useradmin, password: password

## Requirements

- JDK 25 (the project targets Java 25).
- No separate Maven install needed: use the included wrapper, which downloads
  Maven 3.9.11 automatically. Use `./mvnw` on macOS/Linux, `mvnw.cmd` on Windows.

Built with Spring Boot 4.0.6 (Spring Security 7).

## Build and run

Build the executable jar:

```bash
./mvnw clean package
```

Run the application (either option):

```bash
# run directly with the Spring Boot plugin
./mvnw spring-boot:run

# or run the jar produced by the build above
java -jar target/spring-security-0.0.1-SNAPSHOT.jar
```

Then open http://localhost:8080 in your browser and log in with one of the
users listed above.

## The example includes:
- a custom error page (defined in Configuration + controller).
- a (disabled) custom login page (uses login.html in the templates).
To enable the custom login page, uncomment the corresponding
code in the Configuration class and in the controller.

