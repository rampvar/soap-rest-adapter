package com.soaprestadapter.service;

import com.soaprestadapter.Repository.GeneratedWsdlClassRepository;
import com.soaprestadapter.WsdlToClassStorageStrategy;
import com.soaprestadapter.entity.GeneratedWsdlClassEntity;
import java.util.List;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;

@Service
@Qualifier("jpaWsdlStorage")
@Profile("!sqlite")
public class JpaWsdlStorage implements WsdlToClassStorageStrategy {

    /**
     *  Inject repository class for Jpa Wsdl Storage
     */
    private final GeneratedWsdlClassRepository wsdlClassRepository;

    public JpaWsdlStorage(final GeneratedWsdlClassRepository wsdlClassRepository) {
        this.wsdlClassRepository = wsdlClassRepository;
    }

    @Override
    public void save(final GeneratedWsdlClassEntity wsdlClassEntity) {

        wsdlClassRepository.save(wsdlClassEntity);
    }

    @Override
    public List<GeneratedWsdlClassEntity> findAll() {
        return wsdlClassRepository.findAll();
    }
}
