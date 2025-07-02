package com.soaprestadapter;


import com.soaprestadapter.service.UploadWsdlToDatabaseService;
import com.soaprestadapter.service.WsdlGenerationService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
public class WsdlGenerationServiceTest {

    @Mock
    private UploadWsdlToDatabaseService uploadWsdlToDatabaseService;

    @Spy
    @InjectMocks
    private WsdlGenerationService spyProcessor;

    @BeforeEach
    void setup() {
        spyProcessor = Mockito.spy(new WsdlGenerationService(uploadWsdlToDatabaseService));
    }

    @Test
    void testGenerateFromWsdlUrl() throws Exception {
        // Arrange
        String wsdlUrl = "http://example.com/test.wsdl";
        List<String> xsdUrls = List.of("http://example.com/schema1.xsd", "http://example.com/schema2.xsd");

        Path tempDir = Files.createTempDirectory("test_temp");
        Path outputDir = Files.createTempDirectory("test_output");
        Path dummyWsdlPath = Files.createTempFile(tempDir, "test", ".wsdl");

        // Spy the method and mock downloadFile
        Mockito.doReturn(dummyWsdlPath).when(spyProcessor).downloadFile(eq(wsdlUrl), any(Path.class));
        for (String xsdUrl : xsdUrls) {
            Mockito.doReturn(Files.createTempFile(tempDir, "schema", ".xsd"))
                    .when(spyProcessor).downloadFile(eq(xsdUrl), any(Path.class));
        }

        // Stub listFilenamesForWsdl
        Mockito.doReturn(List.of("Test.java")).when(spyProcessor).listFilenamesForWsdl(any(Path.class), eq(".java"));
        Mockito.doReturn(List.of("Test.class")).when(spyProcessor).listFilenamesForWsdl(any(Path.class), eq(".class"));

        List<Path> mockClassPaths = List.of(
                Files.createTempFile("Test", ".class")
        );

        Mockito.doReturn(mockClassPaths).when(spyProcessor).getClassFilePaths(any(Path.class));


        // Mock the upload service call
        Mockito.doNothing().when(uploadWsdlToDatabaseService).uploadWsdlToDb(anyString(), anyList());
        Mockito.doNothing().when(spyProcessor).runWsdlToJava(any());

        // Act
        Map<String, List<String>> result = spyProcessor.generateFromWsdlUrl(wsdlUrl, xsdUrls);

        // Assert
        assertNotNull(result);
        assertTrue(result.containsKey("java"));
        assertTrue(result.containsKey("class"));
        assertEquals(List.of("Test.java"), result.get("java"));
        assertEquals(List.of("Test.class"), result.get("class"));

        // Verify interactions
        verify(uploadWsdlToDatabaseService).uploadWsdlToDb(eq(wsdlUrl), anyList());
    }

    @Test
    void testGenerateFromWsdlUrl_NoGeneratedFiles_ShouldFail() throws Exception {
        // Arrange
        String wsdlUrl = "http://example.com/test.wsdl";
        List<String> xsdUrls = List.of("http://example.com/schema1.xsd");

        Path tempDir = Files.createTempDirectory("test_temp");
        Path dummyWsdlPath = Files.createTempFile(tempDir, "test", ".wsdl");

        Mockito.doReturn(dummyWsdlPath).when(spyProcessor).downloadFile(eq(wsdlUrl), any(Path.class));
        Mockito.doReturn(Files.createTempFile(tempDir, "schema", ".xsd"))
                .when(spyProcessor).downloadFile(eq(xsdUrls.get(0)), any(Path.class));

        // Simulate no .java or .class files generated
        Mockito.doReturn(List.of()).when(spyProcessor).listFilenamesForWsdl(any(Path.class), eq(".java"));
        Mockito.doReturn(List.of()).when(spyProcessor).listFilenamesForWsdl(any(Path.class), eq(".class"));

        Mockito.doReturn(List.of()).when(spyProcessor).getClassFilePaths(any(Path.class));

        Mockito.doNothing().when(uploadWsdlToDatabaseService).uploadWsdlToDb(anyString(), anyList());
        Mockito.doNothing().when(spyProcessor).runWsdlToJava(any());

        // Act
        Map<String, List<String>> result = spyProcessor.generateFromWsdlUrl(wsdlUrl, xsdUrls);

        // Assert
        assertNotNull(result);
        assertTrue(result.containsKey("java"), "Expected key 'java' not found");
        assertTrue(result.containsKey("class"), "Expected key 'class' not found");

    }


}

