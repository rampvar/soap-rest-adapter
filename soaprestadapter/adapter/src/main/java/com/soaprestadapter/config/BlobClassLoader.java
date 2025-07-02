package com.soaprestadapter.config;

import java.util.Map;

public class BlobClassLoader extends ClassLoader {
    /**
     * A map containing class names and their corresponding bytecode.
     */
    private final Map<String, byte[]> classDataMap;

    public BlobClassLoader(final Map<String, byte[]> classDataMap, final  ClassLoader parent) {
        super(parent);
        this.classDataMap = classDataMap;
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
