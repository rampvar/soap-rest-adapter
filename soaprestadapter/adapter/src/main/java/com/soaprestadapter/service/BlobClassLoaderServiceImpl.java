package com.soaprestadapter.service;


import aj.org.objectweb.asm.ClassReader;
import com.soaprestadapter.WsdlToClassStorageStrategy;
import com.soaprestadapter.config.BlobClassLoader;
import com.soaprestadapter.config.BlobClassRegistry;
import com.soaprestadapter.entity.GeneratedWsdlClassEntity;
import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Service;


/**
 * Service for loading and unpacking class bytecode from the database.
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class BlobClassLoaderServiceImpl implements BlobClassLoaderService {

    /** Repository to access stored class blobs. */
    private final WsdlToClassStorageStrategy repository;

    /**
     * Instance of the custom class loader that holds dynamically loaded classes
     * after being loaded from the database.
     */
    private BlobClassLoader blobClassLoader;

    /** Output directory to write class files, defaults to target/classes. */
    private String outputDir;


    /**
     * Shared class loader instance
     */
    private final Map<String, byte[]> runtimeClassData = new ConcurrentHashMap<>();

    /**
     * Loads all class blobs from the DB and dynamically loads them via a custom ClassLoader.
     *
     * @return a BlobClassLoader instance that can load classes loaded from the database.
     */
    @Override
    public BlobClassLoader loadClassesFromDb() {
        List<GeneratedWsdlClassEntity> classes = safeFetchFromDb();
        BlobClassLoader loader = null;

        if (classes == null || classes.isEmpty()) {
            return null;
        }

        Map<String, byte[]> classMap = new HashMap<>();

        try {
            for (GeneratedWsdlClassEntity entity : classes) {
                byte[] classData = entity.getClassData();
                classMap.putAll(unpack(classData));
            }

            loader = new BlobClassLoader(classMap, getClass().getClassLoader());

            for (String className : classMap.keySet()) {
                Class<?> clazz = loader.loadClass(className);
                // ✅ Register the loaded class
                BlobClassRegistry.registerClass(className, clazz);

                log.debug("Loaded: {}", clazz.getName());
            }

        } catch (IOException | URISyntaxException e) {
            log.debug("Error unpacking class blob: {}", e.getMessage(), e);
        } catch (ClassNotFoundException | ClassFormatError e) {
            log.debug("Class not found during dynamic loading: {}", e.getMessage(), e);
        }

        return loader;
    }
    /**
     * Loads a newly inserted class from the database at runtime.
     * Fetches the blob by ID or timestamp or another identifier.
     *@return a BlobClassLoader instance that can load new classes loaded from the database.
     */
    public BlobClassLoader loadNewClassesAtRuntime() {
        List<GeneratedWsdlClassEntity> classes = safeFetchFromDb();
        BlobClassLoader loader = null;

        if (classes == null || classes.isEmpty()) {
            return null;
        }

        Map<String, byte[]> classMap = new HashMap<>();

        try {
            for (GeneratedWsdlClassEntity entity : classes) {
                byte[] classData = entity.getClassData();
                Map<String, byte[]> unpackedClasses = unpack(classData);

                for (Map.Entry<String, byte[]> entry : unpackedClasses.entrySet()) {
                    String className = entry.getKey();

                    // ✅ Skip if class already present in classpath
                    if (isClassOnClasspath(className)) {
                       continue;
                    }

                    classMap.put(className, entry.getValue());
                }
            }

            loader = new BlobClassLoader(classMap, getClass().getClassLoader());

            for (String className : classMap.keySet()) {
                Class<?> clazz = loader.loadClass(className);
                // ✅ Register the loaded class
                BlobClassRegistry.registerClass(className, clazz);

                log.debug("Loaded: {}", clazz.getName());
            }

        } catch (IOException | URISyntaxException e) {
            log.debug("Error unpacking class blob: {}", e.getMessage(), e);
        } catch (ClassNotFoundException | ClassFormatError e) {
            log.debug("Class not found during dynamic loading: {}", e.getMessage(), e);
        }

        return loader;
    }

    /**
     * Unpacks the blob data into a map of class names and byte arrays.
     *
     * @param blobData the blob containing one or more class files
     * @return map of class names to class bytecode
     * @throws IOException in case of malformed blob data
     */
    private Map<String, byte[]> unpack(final byte[] blobData) throws IOException, URISyntaxException {
        Map<String, byte[]> classMap = new HashMap<>();

        outputDir = Paths.get(
                getClass().getProtectionDomain().getCodeSource().getLocation().toURI()
        ).toAbsolutePath().normalize().toString();
        if (outputDir == null || outputDir.isBlank()) {
            throw new IllegalStateException(
                    "Output directory is not set. Configure it as a system property or fallback.");
        }

        try (DataInputStream dis = new DataInputStream(new ByteArrayInputStream(blobData))) {
            while (dis.available() > 0) {
                int nameLen = dis.readInt();
                byte[] nameBytes = new byte[nameLen];
                dis.readFully(nameBytes);
                String fallbackClassName = new String(nameBytes, StandardCharsets.UTF_8);

                int byteLen = dis.readInt();
                byte[] classBytes = new byte[byteLen];
                dis.readFully(classBytes);

                String realClassName;
                try {
                    ClassReader reader = new ClassReader(classBytes);
                    realClassName = reader.getClassName().replace('/', '.');
                } catch (IllegalArgumentException | ArrayIndexOutOfBoundsException e) {
                    log.debug("Failed to parse class bytecode for '{}'. Using fallback. Reason: {}",
                            fallbackClassName, e.toString());
                    realClassName = fallbackClassName;
                }

                Path outputPath = Paths.get(outputDir, realClassName.replace('.', '/') + ".class");
                Files.createDirectories(outputPath.getParent());
                Files.write(outputPath, classBytes);

                classMap.put(realClassName, classBytes);
            }
        }

        return classMap;
    }

    /**
     * Safely fetches class entities from the database with exception handling.
     *
     * @return list of class entities or empty list if DB call fails
     */
    private List<GeneratedWsdlClassEntity> safeFetchFromDb() {
        try {
            return repository.findAll();
        } catch (DataAccessException dae) {
            log.debug("Database fetch failed: {}", dae.getMessage(), dae);
            return Collections.emptyList();
        }
    }
    @Override
    public BlobClassLoader getBlobClassLoader() {
        return this.blobClassLoader;
    }

    private boolean isClassOnClasspath(final String className) {
        try {
            // Uses parent class loader to check classpath
            Class.forName(className, false, getClass().getClassLoader());
            log.info("loaded classnames {}:", className);
            return true;
        } catch (ClassNotFoundException e) {
            return false;
        }
    }
}
