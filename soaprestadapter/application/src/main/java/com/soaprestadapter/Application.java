package com.soaprestadapter;

import com.soaprestadapter.service.UploadWsdlToDatabaseService;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Import;
import org.springframework.core.env.ConfigurableEnvironment;


@SpringBootApplication
public class Application {

    public static void main(final String[] args) throws IOException {
        ApplicationContext context =
                SpringApplication.run(Application.class);
        System.out.println("Hello world");
    }
}
