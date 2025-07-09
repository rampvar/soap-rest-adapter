package com.soaprestadapter.service;

import com.soaprestadapter.WsdlToClassStorageStrategy;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class CobolAttributeServiceImplTest {

    @Mock
    private WsdlToClassStorageStrategy repository;

    @InjectMocks
    private CobolAttributeServiceImpl service;

    @Test
    public void testGetPayloadOne_ReturnsCorrectPayloadForValidOperationName() {
        String operationName = "validOperation";
        String expectedPayload = "expectedPayload";

        when(repository.findPayloadOneByOperationName(operationName)).thenReturn(expectedPayload);

        String actualPayload = service.getPayloadOne(operationName);

        assertEquals(expectedPayload, actualPayload);
    }
}
