package com.soaprestadapter.service;

import com.soaprestadapter.DatabaseService;
import com.soaprestadapter.Repository.CobolAttributeRepository;
import com.soaprestadapter.Repository.FetchResponseCopybookDataRepository;
import com.soaprestadapter.Repository.GeneratedWsdlClassRepository;
import com.soaprestadapter.Repository.SystemConfigDataRepository;
import com.soaprestadapter.Repository.UserRepository;
import com.soaprestadapter.entity.FetchResponseCopybookDataEntity;
import com.soaprestadapter.entity.GeneratedWsdlClassEntity;
import com.soaprestadapter.entity.SystemConfigDataEntity;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;

/**
 * Service class for JPA Wsdl Storage for other than sqlite db.
 */
@Service
@Profile("!sqlite")
@RequiredArgsConstructor
public class DatabaseServiceImpl implements DatabaseService {

    /**
     *  Inject repository class for Jpa Wsdl Storage
     */
    private final GeneratedWsdlClassRepository wsdlClassRepository;

    /**
     *  Inject repository class for cobol attributes
     */
    private final CobolAttributeRepository cobolAttributeRepository;

    /**
     *  Inject repository class
     */
    private final FetchResponseCopybookDataRepository fetchResponseCopybookDataRepository;

    /**
     * User Repository
     */
    private final UserRepository userRepository;

    /**
     * SystemConfigDataRepository
     */
    private final SystemConfigDataRepository systemConfigDataRepository;


    /**
     * Method to save WsdlClassEntity to Jpa storage
     * @param wsdlClassEntity
     */
    @Override
    public void save(final GeneratedWsdlClassEntity wsdlClassEntity) {

        wsdlClassRepository.save(wsdlClassEntity);
    }

    /**
     * Method to find all WsdlClassEntity from Jpa storage
     * @return
     */
    @Override
    public List<GeneratedWsdlClassEntity> findAll() {
        return wsdlClassRepository.findAll();
    }

    /**
     * Method to find all findPayloadOneByOperationName from Jpa storage
     * @return
     */
    @Override
    public String findPayloadOneByOperationName(final String operationName) {
        return cobolAttributeRepository.findPayloadOneByOperationName(operationName);
    }

    /**
     * Method to find all findPayloadTwoByOperationName from Jpa storage
     * @return
     */
    @Override
    public String findPayloadTwoByOperationName(final String operationName) {
        return cobolAttributeRepository.findPayloadTwoByOperationName(operationName);
    }

    /**
     * Method to find entity based on wsdl url
     * @param wsdlUrl as input
     * @return GeneratedWsdlClassEntity
     */
    @Override
    public Optional<GeneratedWsdlClassEntity> findByWsdlUrl(final String wsdlUrl) {
        return wsdlClassRepository.findByWsdlUrl(wsdlUrl);
    }

    /**
     *
     * @param operationName to fetch data for from the db
     * @return
     */
    @Override
    public Optional<FetchResponseCopybookDataEntity> getByOperationName(final String operationName) {
        return fetchResponseCopybookDataRepository.findByOperationName(operationName);

    }

    /**
     * get entity count
     * @return count value
     */
    @Override
    public Optional<Long> getEntityCount() {
        return Optional.of(wsdlClassRepository.count());
    }

    /**
     * Entitle user
     * @param userId
     * @return true or false
     */
    @Override
    public boolean isUserEntitled(final Long userId) {
        return userRepository.isUserEntitled(userId);
    }

    /**
     * @param operationName to fetch data for from the db
     * @return SystemConfigDataEntity
     */
    @Override
    public Optional<SystemConfigDataEntity> findByOperationName
    (final String operationName) {
        return systemConfigDataRepository.findByOperationName(operationName);
    }

    /**
     *
     * @return SystemConfigDataEntity list
     */
    @Override
    public List<SystemConfigDataEntity> findAllForSystemConfig() {
        return systemConfigDataRepository.findAll();
    }

}
