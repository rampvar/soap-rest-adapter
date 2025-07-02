package com.soaprestadapter.service;

import com.soaprestadapter.WsdlToClassStorageStrategy;
import com.soaprestadapter.entity.GeneratedWsdlClassEntity;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import java.io.IOException;
import java.nio.file.*;
import java.util.List;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class UploadWsdlToDatabaseServiceTest {

    private WsdlToClassStorageStrategy storageStrategy;
    private UploadWsdlToDatabaseService service;

    @BeforeEach
    void setUp() {
        storageStrategy = mock(WsdlToClassStorageStrategy.class);
        service = new UploadWsdlToDatabaseService(storageStrategy);
    }

    @Test
    void testUploadWsdlToDb_withValidClassFile() throws IOException {
        Path tempClassFile = Files.createTempFile("TestClass", ".class");
        Files.write(tempClassFile, new byte[]{(byte) 0xCAFEBABE}); // dummy class byte

        service.uploadWsdlToDb("http://dummy.wsdl", List.of(tempClassFile));

        // Verify storageStrategy.save(...) was called with correct entity
        ArgumentCaptor<GeneratedWsdlClassEntity> captor = ArgumentCaptor.forClass(GeneratedWsdlClassEntity.class);
        verify(storageStrategy).save(captor.capture());

        GeneratedWsdlClassEntity savedEntity = captor.getValue();
        assertEquals("http://dummy.wsdl", savedEntity.getWsdlUrl());
        assertNotNull(savedEntity.getClassData());
        assertNotNull(savedEntity.getGeneratedAt());

        Files.deleteIfExists(tempClassFile);
    }

}