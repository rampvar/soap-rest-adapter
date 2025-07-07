package com.soaprestadapter;

import com.soaprestadapter.entity.GeneratedWsdlClassEntity;
import java.util.List;

/**
 * Interface for saving generated WSDL classes to a db based on profile.
 */
public interface WsdlToClassStorageStrategy {

    /**
     *  save method to
     *  store the generated WSDL class based on the profile
     * @param wsdlClassEntity
     */
    void save(GeneratedWsdlClassEntity wsdlClassEntity);

    /**
     * retrieve all saved generated WSDL classes
     * @return    list of saved WSDL classes
     */
    List<GeneratedWsdlClassEntity> findAll();

    String findPayloadOneByOperationName(String operationName);
    String findPayloadTwoByOperationName(String operationName);

}
