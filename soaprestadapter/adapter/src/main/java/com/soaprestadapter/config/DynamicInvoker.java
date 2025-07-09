package com.soaprestadapter.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/**
 * Utility to dynamically invoke methods on DB-loaded classes using reflection.
 */
@Slf4j
@Component
public class DynamicInvoker {

     /**
     * Retrieves a dynamically loaded class from the registry.
     *
     * @param fqcn fully qualified class name
     * @return the loaded {@link Class}, or {@code null} if not found
     */
    public Class<?> getLoadedClass(final String fqcn) {
        return BlobClassRegistry.getClassByName(fqcn);
    }
}
