package com.soaprestadapter.service;

import aj.org.objectweb.asm.ClassReader;
import com.soaprestadapter.Repository.GeneratedWsdlClassRepository;
import com.soaprestadapter.WsdlToClassStorageStrategy;
import com.soaprestadapter.config.BlobClassLoader;
import com.soaprestadapter.entity.GeneratedWsdlClassEntity;
import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Service;


/**
 * Service for loading and unpacking class bytecode from the database.
 */
@Service
public class BlobClassLoaderService {

    /** Logger for this service class. */
    private static final Logger LOGGER  = LoggerFactory.getLogger(BlobClassLoaderService.class);

    /** Repository to access stored class blobs. */
    private final WsdlToClassStorageStrategy repository;

    /**
     * Constructor to inject repository.
     *
     * @param repository the blob storage repository
     */
    public BlobClassLoaderService(final WsdlToClassStorageStrategy repository) {

        this.repository = repository;
    }

    /**
     * Loads all class blobs from DB and dynamically loads them via custom ClassLoader.
     */
    public void loadClassesFromDb() {
        List<GeneratedWsdlClassEntity> classes = safeFetchFromDb();

        if (classes == null || classes.isEmpty()) {
            return;
        }

        Map<String, byte[]> classMap = new HashMap<>();

        // Single try-catch for the full load/unpack process
        try {
            for (GeneratedWsdlClassEntity entity : classes) {
                byte[] classData = entity.getClassData();

                // Let unpack throw IOException — we'll catch it at method level
                classMap.putAll(unpack(classData));
            }

            BlobClassLoader loader = new BlobClassLoader(classMap, getClass().getClassLoader());

            for (String className : classMap.keySet()) {
                Class<?> clazz = loader.loadClass(className);
                LOGGER .info("Loaded: {}", clazz.getName());
            }
        } catch (IOException e) {
            LOGGER .warn("Error unpacking class blob: {}", e.getMessage());
        } catch (ClassNotFoundException e) {
            LOGGER .warn("Class not found during dynamic loading: {}", e.getMessage());
        }
    }

    /**
     * Unpacks the blob data into a map of class names and byte arrays.
     *
     * @param blobData the blob containing one or more class files
     * @return map of class names to class bytecode
     * @throws IOException in case of malformed blob data
     */
    private Map<String, byte[]> unpack(final byte[] blobData) throws IOException {
        Map<String, byte[]> classMap = new HashMap<>();
        try (DataInputStream dis = new DataInputStream(new ByteArrayInputStream(blobData))) {
            while (dis.available() > 0) {
                int nameLen = dis.readInt();
                byte[] nameBytes = new byte[nameLen];
                dis.readFully(nameBytes);
                String fallbackClassName = new String(nameBytes, StandardCharsets.UTF_8); // original name from storage

                int byteLen = dis.readInt();
                byte[] classBytes = new byte[byteLen];
                dis.readFully(classBytes);

                // ✅ Use ASM to extract actual fully qualified class name from bytecode
                String realClassName;
                try {
                    ClassReader reader = new ClassReader(classBytes);
                    realClassName = reader.getClassName().replace('/', '.');
                } catch (IllegalArgumentException e) {
                    // fallback to stored class name if ASM fails
                    realClassName = fallbackClassName;
                }

                String outputDir = "C:/output/classes";  // Change this to your actual desired directory
                Path outputPath = Paths.get(outputDir, realClassName.replace('.', '/') + ".class");
                Files.createDirectories(outputPath.getParent()); // ensure folder exists
                Files.write(outputPath, classBytes);
                classMap.put(realClassName, classBytes);
            }
        }
        return classMap;
    }

    private List<GeneratedWsdlClassEntity> safeFetchFromDb() {
        try {
            return repository.findAll();
        } catch (DataAccessException dae) {
            LOGGER .error("Database fetch failed: {}", dae.getMessage());
            return Collections.emptyList();
        }
    }
}


