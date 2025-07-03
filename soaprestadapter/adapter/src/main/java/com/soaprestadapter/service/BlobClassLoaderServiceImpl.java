package com.soaprestadapter.service;

import aj.org.objectweb.asm.ClassReader;
import com.soaprestadapter.WsdlToClassStorageStrategy;
import com.soaprestadapter.config.BlobClassLoader;
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
   
    private  final WsdlToClassStorageStrategy repository;

    /**
     * The directory path where class files will be written to at runtime.
     * Defaults to the standard Maven `target/classes` if not explicitly configured.
     */
    private String outputDir;

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
                log .info("Loaded: {}", clazz.getName());
            }
        } catch (IOException | URISyntaxException e) {
            log .warn("Error unpacking class blob: {}", e.getMessage());
        } catch (ClassNotFoundException | ClassFormatError  e) {
            log .warn("Class not found during dynamic loading: {}", e.getMessage());
        }
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
        outputDir =  Paths.get(
                getClass().getProtectionDomain().getCodeSource().getLocation().toURI()
        ).toAbsolutePath().normalize().toString();
        log.info("outputDir*********: {}", outputDir);

        if (outputDir == null || outputDir.isBlank()) {
            throw new IllegalStateException(
                    "System property 'db.classes.output.dir' is not set. " +
                            "Please configure it in pom.xml or as JVM argument.");
        }

        try (DataInputStream dis = new DataInputStream(new ByteArrayInputStream(blobData))) {
            while (dis.available() > 0) {
                int nameLen = dis.readInt();
                byte[] nameBytes = new byte[nameLen];
                dis.readFully(nameBytes);
                String fallbackClassName = new String(nameBytes, StandardCharsets.UTF_8); // original name from storage

                int byteLen = dis.readInt();
                byte[] classBytes = new byte[byteLen];
                dis.readFully(classBytes);

                String realClassName;
                try {
                    ClassReader reader = new ClassReader(classBytes);
                    realClassName = reader.getClassName().replace('/', '.');
                } catch (IllegalArgumentException | ArrayIndexOutOfBoundsException e) {
                    log.warn(
                            "Failed to parse class bytecode for '{}'. Falling back to stored name. Reason: {}",
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


    private List<GeneratedWsdlClassEntity> safeFetchFromDb() {
        try {
            return repository.findAll();
        } catch (DataAccessException dae) {
            log .error("Database fetch failed: {}", dae.getMessage());
            return Collections.emptyList();
        }
    }
}


