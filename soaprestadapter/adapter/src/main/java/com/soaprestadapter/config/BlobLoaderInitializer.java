package com.soaprestadapter.config;

import com.soaprestadapter.service.BlobClassLoaderService;
import com.soaprestadapter.service.LoadClassesAtBootstrapService;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;

/**
 * Initializes the BlobClassLoaderService at application startup.
 * Ensures that dynamically stored classes are loaded from the database
 * before the application begins serving requests.
 */
@Configuration
@RequiredArgsConstructor
public class BlobLoaderInitializer {

    /**
     * Service responsible for loading WSDL-generated classes from the database.
     */
    private final BlobClassLoaderService blobClassLoaderService;

    /**
     * Service responsible for generating and loading .class files from wsdl
     */
    private final LoadClassesAtBootstrapService loadClassesAtBootstrapService;

    /**
     * Loads classes from the database during application startup.
     * This method is automatically invoked by Spring via {@code @PostConstruct}.
     */
    @PostConstruct
    public void init() throws Exception {
        loadClassesAtBootstrapService.loadClassAtBootstrap();
        blobClassLoaderService.loadClassesFromDb();
    }
}
