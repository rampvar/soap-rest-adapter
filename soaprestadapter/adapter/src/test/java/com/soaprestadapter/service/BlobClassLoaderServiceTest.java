/*
package com.soaprestadapter.service;

import aj.org.objectweb.asm.ClassReader;
import com.soaprestadapter.Repository.GeneratedWsdlClassRepository;
import com.soaprestadapter.entity.GeneratedWsdlClassEntity;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.dao.DataAccessException;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.util.*;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class BlobClassLoaderServiceTest {

    @Mock
    private GeneratedWsdlClassRepository repository;

    @InjectMocks
    private BlobClassLoaderService service;

    private static final Path CLASS_DIR = Paths.get("C:/Users/aakansha.aashu/Downloads/generated/generated/org/mulesoft/tshirt_service_classes");

    @Test
    void testLoadClassesFromDb_WithDynamicClassNames() throws Exception {
        List<GeneratedWsdlClassEntity> entitiesFromDb = null; // Simulate DB return (could also be empty list)

        GeneratedWsdlClassEntity fallbackEntity = createFallbackEntityFromLocalClasses();

        List<GeneratedWsdlClassEntity> dataToReturn =
                (entitiesFromDb != null && !entitiesFromDb.isEmpty())
                        ? entitiesFromDb
                        : List.of(fallbackEntity);

        when(repository.findAll()).thenReturn(dataToReturn);

        service.loadClassesFromDb();

        verify(repository, times(1)).findAll();
    }

    private GeneratedWsdlClassEntity createFallbackEntityFromLocalClasses() throws IOException {
        List<Path> classFiles = Files.walk(CLASS_DIR)
                .filter(path -> path.toString().endsWith(".class"))
                .toList();

        ByteArrayOutputStream byteOut = new ByteArrayOutputStream();
        DataOutputStream dos = new DataOutputStream(byteOut);

        for (Path classFile : classFiles) {
            byte[] classBytes = Files.readAllBytes(classFile);
            String className = extractClassName(classBytes); // Internal name
            byte[] nameBytes = className.getBytes(StandardCharsets.UTF_8);

            dos.writeInt(nameBytes.length);
            dos.write(nameBytes);
            dos.writeInt(classBytes.length);
            dos.write(classBytes);
        }

        byte[] blob = byteOut.toByteArray();

        GeneratedWsdlClassEntity entity = new GeneratedWsdlClassEntity();
        entity.setId(999L);
        entity.setWsdlUrl("fallback-local");
        //entity.setClassName("dummy-class");
        entity.setClassData(blob);
        entity.setGeneratedAt(LocalDateTime.now());

        return entity;
    }

    private String extractClassName(byte[] classBytes) {
        ClassReader reader = new ClassReader(classBytes);
        return reader.getClassName().replace('/', '.');
    }

    @Test
    public void testLoadClassesFromDb_EmptyList() {
          when(repository.findAll()).thenThrow(new DataAccessException("DB error") {});

            // Call the method, it should handle the exception gracefully
            service.loadClassesFromDb();

            // Just verify that it tried to call the repo
            verify(repository, times(1)).findAll();
    }
    @Test
    void testLoadClassesFromDb_CorruptBlob() {
        GeneratedWsdlClassEntity entity = new GeneratedWsdlClassEntity();
        entity.setId(1L);
        entity.setClassData(new byte[]{1, 2, 3}); // invalid format
        entity.setGeneratedAt(LocalDateTime.now());

        when(repository.findAll()).thenReturn(List.of(entity));

        service.loadClassesFromDb();

        verify(repository, times(1)).findAll();
    }
    @Test
    void testLoadClassesFromDb_ClassNotFoundException() throws IOException {
        // Create a valid class name but omit actual bytecode to trigger ClassNotFoundException
        String fakeClassName = "com.nonexistent.ClassTest";
        byte[] nameBytes = fakeClassName.getBytes(StandardCharsets.UTF_8);
        byte[] fakeClassBytes = {};  // empty byte array to simulate missing class data

        ByteArrayOutputStream byteOut = new ByteArrayOutputStream();
        DataOutputStream dos = new DataOutputStream(byteOut);
        dos.writeInt(nameBytes.length);
        dos.write(nameBytes);
        dos.writeInt(fakeClassBytes.length);
        dos.write(fakeClassBytes);  // writing 0 bytes

        GeneratedWsdlClassEntity entity = new GeneratedWsdlClassEntity();
        entity.setId(777L);
        //entity.setClassName(fakeClassName);
        entity.setClassData(byteOut.toByteArray());

        when(repository.findAll()).thenReturn(List.of(entity));

        // Call the method, which should attempt and fail to load class
        service.loadClassesFromDb();

        verify(repository, times(1)).findAll();
    }

}
*/
