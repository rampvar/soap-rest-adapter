package com.soaprestadapter.service;

import com.soaprestadapter.config.BlobClassLoader;

/**
 * Service interface for dynamically loading compiled classes from the database
 * and exposing a custom class loader.
 */
public interface BlobClassLoaderService {

    /**
     * Loads compiled class files from the database and initializes a custom class loader.
     * @return a {@link BlobClassLoader} instance that contains all dynamically loaded classes
     */
    BlobClassLoader loadClassesFromDb();

    /**
     * Loads compiled class files from the database and initializes a custom class loader.
     *
     * @return a {@link BlobClassLoader} instance that contains all newly loaded classes at runtime
     */
    BlobClassLoader loadNewClassesAtRuntime();

    /**
     * Returns the current {@link BlobClassLoader} instance previously loaded from the database.
     *
     * @return the cached {@link BlobClassLoader} containing loaded classes
     */
    BlobClassLoader getBlobClassLoader();
}
