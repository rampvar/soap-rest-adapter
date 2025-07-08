package com.soaprestadapter.service;

import com.soaprestadapter.Repository.CobolAttributeRepository;
import com.soaprestadapter.Repository.GeneratedWsdlClassRepository;
import com.soaprestadapter.WsdlToClassStorageStrategy;
import com.soaprestadapter.entity.GeneratedWsdlClassEntity;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;

/**
 * Service class for JPA Wsdl Storage for other than sqlite db.
 */
@Service
@Qualifier("jpaWsdlStorage")
@Profile("!sqlite")
@RequiredArgsConstructor
public class JpaWsdlStorage implements WsdlToClassStorageStrategy {

    /**
     *  Inject repository class for Jpa Wsdl Storage
     */
    private final GeneratedWsdlClassRepository wsdlClassRepository;

    /**
     *  Inject repository class for cobol attributes
     */
    private final CobolAttributeRepository cobolAttributeRepository;


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
}
