package com.soaprestadapter.service;

import com.soaprestadapter.Repository.CobolAttributeRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class CobolAttributeServiceImpl implements CobolAttributeService {

    private final CobolAttributeRepository repository;
    @Override
    public String getPayloadOne(String operationName) {
        return repository.findPayloadOneByOperationName(operationName);
    }

    @Override
    public String getPayloadTwo(String operationName) {
        return repository.findPayloadTwoByOperationName(operationName);
    }
}
