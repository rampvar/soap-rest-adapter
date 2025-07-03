package com.soaprestadapter;

import com.soaprestadapter.request.WsdlJobRequest;
import com.soaprestadapter.service.UploadWsdlToDatabaseService;
import com.soaprestadapter.service.WsdlGenerationService;
import com.soaprestadapter.service.WsdlGenerationServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class WsdlGenerationServiceImplTest {

    @Mock
    private UploadWsdlToDatabaseService uploadWsdlToDatabaseService;

    private WsdlGenerationService wsdlGenerationService;

    @BeforeEach
    public void setUp() {
        wsdlGenerationService = new WsdlGenerationServiceImpl(uploadWsdlToDatabaseService);
    }

    @Test
    public void testProcessWsdlUrls_singleJob() throws Exception {
        // Given
        Mockito.doNothing().when(uploadWsdlToDatabaseService).uploadWsdlToDb(anyString(), anyList());

        String dummyWsdlUrl = "https://raw.githubusercontent.com/raghavM16/test/main/serviceWorking.wsdl";
        WsdlJobRequest request = new WsdlJobRequest();
        request.setWsdlUrl(dummyWsdlUrl);
        request.setXsdUrls(Collections.emptyList());
        List<WsdlJobRequest> requests = List.of(request);

        // When
        Map<String, Map<String, List<String>>> result = wsdlGenerationService.processWsdlUrls(requests);

        // Then
        assertNotNull(result);
        assertEquals(1, result.size());
        Map<String, List<String>> jobResult = result.get("job_1");
        assertNotNull(jobResult);
        assertEquals(2, jobResult.size());
        assertNotNull(jobResult.get("java"));
        assertNotNull(jobResult.get("class"));
        verify(uploadWsdlToDatabaseService, times(1)).uploadWsdlToDb(any(), anyList());
    }

    @Test
    public void testProcessWsdlUrls_multipleJobs() throws Exception {
        // Given
        String dummyWsdlUrl = "https://www.example.com/test.wsdl";
        WsdlJobRequest request = new WsdlJobRequest();
        request.setWsdlUrl(dummyWsdlUrl);
        request.setXsdUrls(Collections.emptyList());

        List<WsdlJobRequest> requests = List.of(request);

        // When
        Map<String, Map<String, List<String>>> result = wsdlGenerationService.processWsdlUrls(requests);

        // Then
        assertNotNull(result);
        assertEquals(1, result.size());

        Map<String, List<String>> jobResult1 = result.get("job_1");
        assertNotNull(jobResult1);
        assertEquals(0, jobResult1.size());
    }
}
