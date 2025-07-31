package com.soaprestadapter;

import com.soaprestadapter.entity.FetchResponseCopybookDataEntity;
import com.soaprestadapter.entity.GeneratedWsdlClassEntity;
import com.soaprestadapter.entity.SystemConfigDataEntity;
import java.util.List;
import java.util.Optional;
import org.springframework.data.repository.query.Param;

/**
 *
 */
public interface DatabaseService {


    /**
     * save method to
     * store the generated WSDL class based on the profile
     *
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
     *
     * @param operationName
     * @return string of the payload for the given operation name
     */
    String findPayloadOneByOperationName(String operationName);

    /**
     * findPayloadTwoByOperationName
     *
     * @param operationName
     * @return string of the payload for the given operation name
     */
    String findPayloadTwoByOperationName(String operationName);

    /**
     * findByWsdlUrl check if an entity already exists or not
     *
     * @param wsdlUrl as input
     * @return entity class if present
     */
    Optional<GeneratedWsdlClassEntity> findByWsdlUrl(String wsdlUrl);

    /**
     * retrieve copybook data by operation name
     *
     * @param operationName to fetch data for  from the db
     * @return row based on operation name
     */
    Optional<FetchResponseCopybookDataEntity> getByOperationName(String operationName);

    /**
     * Get entity count
     * @return count value
     */
    Optional<Long> getEntityCount();

    /**
     * check user Id is entitled
     * @param userId
     * @return boolean true or false
     */
    boolean isUserEntitled(@Param("userId") Long userId);

    /**
     * retrieve system config data by operation name
     * @param operationName to fetch data for from the db
     * @return row based on operation name
     */
    Optional<SystemConfigDataEntity> findByOperationName(String operationName);

    /**
     *
     * @return SystemConfigDataEntity list
     */
    List<SystemConfigDataEntity> findAllForSystemConfig();
}
