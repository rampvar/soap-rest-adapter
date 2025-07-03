package com.soaprestadapter.service;

import com.soaprestadapter.WsdlToClassStorageStrategy;
import com.soaprestadapter.entity.GeneratedWsdlClassEntity;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.*;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class UploadWsdlToDatabaseServiceTest {

    private UploadWsdlToDatabaseService uploadWsdlToDatabaseService;

    @Mock
    private WsdlToClassStorageStrategy storageStrategy;

    @Mock
    private BlobClassLoaderServiceImpl classLoaderService;

    @Captor
    private ArgumentCaptor<GeneratedWsdlClassEntity> entityCaptor;

    @BeforeEach
    public void setUp() {
        uploadWsdlToDatabaseService = new UploadWsdlToDatabaseServiceImpl(storageStrategy, classLoaderService);
    }

    @Test
    public void shouldThrowExceptionForEmptyClassFileList() {
        String wsdlUrl = "http://example.com/wsdl";
        List<Path> filesPath = Collections.emptyList();

        assertThrows(IllegalArgumentException.class, () ->
                uploadWsdlToDatabaseService.uploadWsdlToDb(wsdlUrl, filesPath));

        verify(storageStrategy, never()).save(any());
    }

    @Test
    public void shouldThrowExceptionWhenWsdlUrlIsNullOrEmpty() {
        assertThrows(IllegalArgumentException.class, () ->
                uploadWsdlToDatabaseService.uploadWsdlToDb("", Collections.emptyList()));
    }

    @Test
    public void shouldSkipNonClassFiles() throws IOException {
        String wsdlUrl = "http://example.com/wsdl";
        Path txtFile = Paths.get("target/test-classes/sample.txt");
        Files.write(txtFile, "This is a text file".getBytes());

        uploadWsdlToDatabaseService.uploadWsdlToDb(wsdlUrl, List.of(txtFile));

        verify(storageStrategy, never()).save(any());
    }

    @Test
    public void shouldHandleSingleValidClassFile() throws IOException {
        String wsdlUrl = "http://example.com/wsdl";
        Path classFile = Paths.get("target/test-classes/ValidClass.class");
        byte[] classData = "test bytecode".getBytes();
        Files.write(classFile, classData);

        uploadWsdlToDatabaseService.uploadWsdlToDb(wsdlUrl, List.of(classFile));

        verify(storageStrategy).save(entityCaptor.capture());
        GeneratedWsdlClassEntity captured = entityCaptor.getValue();

        assertEquals(wsdlUrl, captured.getWsdlUrl());

        byte[] blob = captured.getClassData();

        // Validate blob content structure
        int nameLen = blob[0] << 24 | (blob[1] & 0xFF) << 16 | (blob[2] & 0xFF) << 8 | (blob[3] & 0xFF);
        String fileName = classFile.getFileName().toString();
        assertEquals(fileName.length(), nameLen);

        String fileNameFromBlob = new String(blob, 4, nameLen, StandardCharsets.UTF_8);
        assertEquals(fileName, fileNameFromBlob);
    }

    @Test
    public void shouldSkipWhenFileIsNotRegularFile() throws IOException {
        String wsdlUrl = "http://example.com/wsdl";
        Path directory = Paths.get("target/test-classes/directoryAsFile");

        Files.createDirectories(directory); // this will not be a regular file

        uploadWsdlToDatabaseService.uploadWsdlToDb(wsdlUrl, List.of(directory));

        verify(storageStrategy, never()).save(any());
    }

    @Test
    public void shouldNotSaveIfAllFilesAreInvalid() throws IOException {
        String wsdlUrl = "http://example.com/wsdl";
        Path invalidFile = Paths.get("target/test-classes/invalid_file.txt");
        Files.write(invalidFile, "non-class content".getBytes());

        uploadWsdlToDatabaseService.uploadWsdlToDb(wsdlUrl, List.of(invalidFile));

        verify(storageStrategy, never()).save(any());
    }

    @Test
    public void shouldHandleClassFilesWithDifferentEncodings() throws IOException {
        String wsdlUrl = "http://example.com/wsdl";
        Path utf16ClassFile = Paths.get("target/test-classes/TestClassUtf16.class");

        Files.write(utf16ClassFile, "dummy data".getBytes(StandardCharsets.UTF_16));

        uploadWsdlToDatabaseService.uploadWsdlToDb(wsdlUrl, List.of(utf16ClassFile));

        verify(storageStrategy).save(entityCaptor.capture());
        GeneratedWsdlClassEntity captured = entityCaptor.getValue();
        assertEquals(wsdlUrl, captured.getWsdlUrl());
        assertTrue(captured.getClassData().length > 0);
    }

    @Test
    public void shouldLogErrorOnDatabaseFailureButStillTry() throws IOException {
        String wsdlUrl = "http://example.com/wsdl";
        Path classFile = Paths.get("target/test-classes/DbFailClass.class");
        Files.write(classFile, "some content".getBytes());

        doThrow(new RuntimeException("DB connection lost")).when(storageStrategy).save(any());

        assertThrows(RuntimeException.class, () ->
                uploadWsdlToDatabaseService.uploadWsdlToDb(wsdlUrl, List.of(classFile)));

        verify(storageStrategy).save(entityCaptor.capture());
    }
}
