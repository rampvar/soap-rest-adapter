package com.soaprestadapter;

import com.soaprestadapter.entity.GeneratedWsdlClassEntity;
import java.util.List;

public interface WsdlToClassStorageStrategy {

    void save(GeneratedWsdlClassEntity wsdlClassEntity);

    List<GeneratedWsdlClassEntity> findAll();

}
