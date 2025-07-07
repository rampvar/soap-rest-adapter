package com.soaprestadapter.service;

/**
 * CobolAttributeService interface representing the Cobol attributes operations.
 */
public interface CobolAttributeService {

    /**
     * getPayloadOne method
     *
     * @param operationName
     * @return string
     */
    String getPayloadOne(String operationName);

    /**
     * getPayloadTwo method
     *
     * @param operationName
     * @return string
     */
    String getPayloadTwo(String operationName);

}
