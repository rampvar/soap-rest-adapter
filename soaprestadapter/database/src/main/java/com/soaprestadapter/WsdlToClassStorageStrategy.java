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

    /**
     * findPayloadOneByOperationName
     * @param operationName
     * @return string of the payload for the given operation name
     */
    String findPayloadOneByOperationName(String operationName);

    /**
     * findPayloadTwoByOperationName
     * @param operationName
     * @return string of the payload for the given operation name
     */
    String findPayloadTwoByOperationName(String operationName);


}
