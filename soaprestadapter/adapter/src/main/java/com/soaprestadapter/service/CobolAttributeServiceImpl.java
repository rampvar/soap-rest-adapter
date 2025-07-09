package com.soaprestadapter.service;

import com.soaprestadapter.WsdlToClassStorageStrategy;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

/**
 * CobolAttributeServiceImpl implementation representing the Cobol attributes operations.
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class CobolAttributeServiceImpl implements CobolAttributeService {

    /**
     * CobolAttributeRepository
     */
    //private final CobolAttributeRepository repository;
    private  final WsdlToClassStorageStrategy repository;

    /**
     * getPayloadOne method
     *
     * @param operationName
     * @Return string
     */
    @Override
    public String getPayloadOne(final String operationName) {
        return repository.findPayloadOneByOperationName(operationName);
    }

    /**
     * getPayloadTwo method
     *
     * @param operationName
     * @Return string
     */
    @Override
    public String getPayloadTwo(final String operationName) {
        return repository.findPayloadTwoByOperationName(operationName);
    }
}
