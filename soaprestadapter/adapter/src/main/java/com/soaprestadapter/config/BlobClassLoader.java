package com.soaprestadapter.config;

import java.util.Map;

/**
 * Custom class loader to load classes from a map of class names and their corresponding bytecode.
 */
public class BlobClassLoader extends ClassLoader {
    /**
     * A map containing class names and their corresponding bytecode.
     */
    private final Map<String, byte[]> classDataMap;

    /**
     *  Constructor to initialize the custom class loader with a map of class names and their bytecode.
     * @param classMap
     * @param parent
     */
    public BlobClassLoader(final Map<String, byte[]> classMap, final  ClassLoader parent) {
        super(parent);
        this.classDataMap = classMap;
    }

    @Override
    protected Class<?> findClass(final String name) throws ClassNotFoundException {
        byte[] bytes = classDataMap.get(name);
        if (bytes == null || bytes.length == 0) {
            throw new ClassNotFoundException("Class not found: " + name);
        }
        return defineClass(name, bytes, 0, bytes.length);
    }

}
