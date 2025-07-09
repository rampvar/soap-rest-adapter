package com.soaprestadapter.config;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * A registry for dynamically loaded classes. Stores class names and their corresponding {@link Class} objects.
 * Used to make dynamically loaded classes accessible across the application.
 */
public class BlobClassRegistry {

    /**
     * A map that holds the fully qualified class name as the key and its corresponding
     * {@link Class} object as the value.
     */
    private static final Map<String, Class<?>> CLASS_MAP = new ConcurrentHashMap<>();

    /**
     * Registers a dynamically loaded class.
     *
     * @param name  the fully qualified class name
     * @param clazz the {@link Class} object to register
     */
    public static void registerClass(final String name, final Class<?> clazz) {
        CLASS_MAP.put(name, clazz);
    }

    /**
     * Retrieves a registered class by its name.
     *
     * @param name the fully qualified class name
     * @return the {@link Class} object, or {@code null} if not found
     */
    public static Class<?> getClassByName(final String name) {
        return CLASS_MAP.get(name);
    }

    /**
     * Returns an unmodifiable view of all registered classes.
     *
     * @return a map of all class names to their {@link Class} objects
     */
    public static Map<String, Class<?>> getAllClasses() {
        return CLASS_MAP; // You may wrap it with Collections.unmodifiableMap(CLASS_MAP) if immutability is required
    }
}
