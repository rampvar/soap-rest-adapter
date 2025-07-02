package com.soaprestadapter.service;

import com.soaprestadapter.WsdlToClassStorageStrategy;
import com.soaprestadapter.entity.GeneratedWsdlClassEntity;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import org.junit.jupiter.api.extension.ExtendWith;

@ExtendWith(MockitoExtension.class)
class UploadWsdlToDatabaseServiceTest {

    @Mock
    private WsdlToClassStorageStrategy storageStrategy;

    @InjectMocks
    private UploadWsdlToDatabaseService uploadService;

    @Test
    void testUploadWsdlToDb_withClassFiles(@TempDir Path tempDir) throws IOException {
        // Arrange
        String wsdlUrl = "http://example.com/wsdl";
        Path classFile = tempDir.resolve("TestClass.class");
        byte[] mockClassBytes = new byte[]{(byte) 0xCAFEBABE, 0x00, 0x00, 0x00, 0x34};
        Files.write(classFile, mockClassBytes);

        // Act
        List<Path> files = Collections.singletonList(classFile);

        // Act
        uploadService.uploadWsdlToDb(wsdlUrl, files);

        // Assert
        ArgumentCaptor<GeneratedWsdlClassEntity> captor = ArgumentCaptor.forClass(GeneratedWsdlClassEntity.class);
        verify(storageStrategy, times(1)).save(captor.capture());

        GeneratedWsdlClassEntity savedEntity = captor.getValue();
        assertNotNull(savedEntity);
        assertEquals(wsdlUrl, savedEntity.getWsdlUrl());
        assertNotNull(savedEntity.getClassData());
        assertTrue(savedEntity.getClassData().length > 0);
        assertNotNull(savedEntity.getGeneratedAt());

        // Cleanup
        Files.deleteIfExists(classFile);
        Files.deleteIfExists(tempDir);
    }
}

