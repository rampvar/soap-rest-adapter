package com.soaprestadapter.service;

import com.soaprestadapter.Repository.SystemConfigDataRepository;
import com.soaprestadapter.SystemConfigDataStrategy;
import com.soaprestadapter.entity.SystemConfigDataEntity;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;

/**
 * JpaSystemConfig
 */
@RequiredArgsConstructor
@Service
@Profile("!sqlite")
public class JpaSystemConfig implements SystemConfigDataStrategy {

    /**
     * SystemConfigDataRepository
     */
    private final SystemConfigDataRepository systemConfigDataRepository;

    /**
     * @param operationName to fetch data for from the db
     * @return SystemConfigDataEntity
     */
    @Override
    public SystemConfigDataEntity findByOperationName
    (final String operationName) {
        return systemConfigDataRepository.findByOperationName(operationName);
    }


}
