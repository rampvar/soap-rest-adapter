package com.soaprestadapter;

import com.soaprestadapter.entity.SystemConfigDataEntity;

/**
 * Interface for getting system config data from db.
 */
public interface SystemConfigDataStrategy {

    /**
     * retrieve system config data by operation name
     * @param operationName to fetch data for from the db
     * @return row based on operation name
     */
    SystemConfigDataEntity findByOperationName(String operationName);

}