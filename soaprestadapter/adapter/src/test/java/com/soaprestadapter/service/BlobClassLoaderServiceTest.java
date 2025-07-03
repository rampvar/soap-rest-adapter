package com.soaprestadapter.service;

import com.soaprestadapter.entity.GeneratedWsdlClassEntity;
import com.soaprestadapter.WsdlToClassStorageStrategy;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
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
import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class BlobClassLoaderServiceTest {

    @Mock
    private WsdlToClassStorageStrategy repository;

    @InjectMocks
    private BlobClassLoaderServiceImpl service; // Mockito will inject `repository` here

    @BeforeEach
    public void setUp() {
        System.setProperty("db.classes.output.dir", "target/classes");
       }


    @Test
    void testLoadClassesFromDb_WithValidMockBlob() throws IOException {
        GeneratedWsdlClassEntity entity = new GeneratedWsdlClassEntity();
        entity.setId(999L);
        entity.setWsdlUrl("mock-url");
        entity.setGeneratedAt(LocalDateTime.now());
        entity.setClassData(createMockClassBlob("com.example.ValidClass"));

        when(repository.findAll()).thenReturn(List.of(entity));

        assertDoesNotThrow(() -> service.loadClassesFromDb());
        verify(repository, times(1)).findAll();
    }

    @Test
    void testLoadClassesFromDb_CorruptBlob() {
        GeneratedWsdlClassEntity entity = new GeneratedWsdlClassEntity();
        entity.setId(1L);
        entity.setClassData(new byte[]{1, 2, 3}); // invalid format
        entity.setGeneratedAt(LocalDateTime.now());

        when(repository.findAll()).thenReturn(List.of(entity));

        assertDoesNotThrow(() -> service.loadClassesFromDb());
        verify(repository, times(1)).findAll();
    }

    @Test
    void testLoadClassesFromDb_ClassNotFoundException() throws IOException {
        String fakeClassName = "com.fake.ClassThatFails";
        byte[] classBlob = createMockClassBlob(fakeClassName, new byte[]{0x01, 0x02}); // not real bytecode

        GeneratedWsdlClassEntity entity = new GeneratedWsdlClassEntity();
        entity.setId(777L);
        entity.setClassData(classBlob);
        entity.setGeneratedAt(LocalDateTime.now());

        when(repository.findAll()).thenReturn(List.of(entity));

        assertDoesNotThrow(() -> service.loadClassesFromDb());
        verify(repository, times(1)).findAll();
    }

    @Test
    void testLoadClassesFromDb_DbThrowsException() {
        when(repository.findAll()).thenThrow(new DataAccessException("DB failure") {});
        assertDoesNotThrow(() -> service.loadClassesFromDb());
        verify(repository, times(1)).findAll();
    }

    // ----------- Utility Method to Create Fake Class Blobs ------------ //

    private byte[] createMockClassBlob(String className) throws IOException {
        return createMockClassBlob(className, new byte[16]); // 16 dummy bytes
    }

    private byte[] createMockClassBlob(String className, byte[] classBytes) throws IOException {
        ByteArrayOutputStream byteOut = new ByteArrayOutputStream();
        DataOutputStream dos = new DataOutputStream(byteOut);

        byte[] nameBytes = className.getBytes(StandardCharsets.UTF_8);

        dos.writeInt(nameBytes.length);
        dos.write(nameBytes);
        dos.writeInt(classBytes.length);
        dos.write(classBytes);

        return byteOut.toByteArray();
    }
}
