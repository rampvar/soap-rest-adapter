package com.soaprestadapter.config;

import com.soaprestadapter.service.BlobClassLoaderService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Configuration class for exposing {@link BlobClassLoader} as a Spring Bean
 * after loading classes from the database.
 */
@Configuration
public class ClassLoaderConfig {

    /**
     * Service responsible for loading class bytecode from the database.
     */
    private final BlobClassLoaderService service;

    /**
     * Constructor that injects {@link BlobClassLoaderService}.
     *
     * @param blobClassLoaderService the service used to load class bytecode from the database
     */
    public ClassLoaderConfig(final BlobClassLoaderService blobClassLoaderService) {
        this.service = blobClassLoaderService;
    }

    /**
     * Defines a Spring bean for {@link BlobClassLoader}.
     *
     * @return a BlobClassLoader instance loaded with DB classes
     */
    @Bean
    public BlobClassLoader blobClassLoader() {
        return service.loadClassesFromDb();
    }
}
