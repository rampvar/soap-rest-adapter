package com.soaprestadapter;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;

/**
 * Main class for the Spring Boot application.
 */
@SpringBootApplication
public class Application {

    /**
     * Entry point for the Spring Boot application.
     * @param args
     */
    public static void main(final String[] args) {
        ApplicationContext context = SpringApplication.run(Application.class);
    }
}
