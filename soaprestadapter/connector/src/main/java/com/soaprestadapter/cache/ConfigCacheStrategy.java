package com.soaprestadapter.cache;

/**
 * ConfigCacheStrategy Interface which should be implemented
 * to enable different cache stratergy.
 */
public interface ConfigCacheStrategy {

    /**
     * @param key1 operationName
     * @return operationEndpointUrl
     */
    String getConfig(String key1);

    /**
     * preload the cache during runtime
     */
    void preload();
}
