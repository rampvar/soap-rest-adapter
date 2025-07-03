package com.soaprestadapter.service;

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
}
