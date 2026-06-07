package hac.springsecurity;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Application entry point.
 *
 * {@code @SpringBootApplication} is a convenience annotation that bundles three others:
 *   - @Configuration            : this class may itself declare @Bean definitions
 *   - @EnableAutoConfiguration  : Spring Boot auto-configures beans from the jars on the classpath.
 *                                 Because spring-boot-starter-security is present, the whole
 *                                 security filter chain is wired up for us automatically.
 *   - @ComponentScan            : scan the "hac.springsecurity" package for @Controller,
 *                                 @Configuration, @Service, ... and register them as beans.
 */
@SpringBootApplication
public class SpringSecurityApplication {

    public static void main(String[] args) {
        // Bootstraps the Spring application context and starts the embedded web server (Tomcat).
        SpringApplication.run(SpringSecurityApplication.class, args);
    }

}
