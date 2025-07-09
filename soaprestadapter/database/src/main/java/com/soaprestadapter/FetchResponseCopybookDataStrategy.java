package com.soaprestadapter;

import com.soaprestadapter.entity.FetchResponseCopybookDataEntity;
import java.util.List;

/**
 * Interface for saving generated WSDL classes to a db based on profile.
 */
public interface FetchResponseCopybookDataStrategy {

    /**
     *  save method to
     *  store the generated copybook data
     * @param fetchResponseCopybookDataEntity to save in the db
     */
    void save(FetchResponseCopybookDataEntity fetchResponseCopybookDataEntity);

    /**
     * retrieve all saved generated data
     * @return    list of saved data
     */
    List<FetchResponseCopybookDataEntity> findAll();

    /**
     * retrieve copybook data by operation name
     *
     * @param operationName to fetch data for  from the db
     * @return row based on operation name
     */
    FetchResponseCopybookDataEntity getByOperationName(String operationName);

}