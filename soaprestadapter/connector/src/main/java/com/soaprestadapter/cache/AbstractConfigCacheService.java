package com.soaprestadapter.cache;

import jakarta.annotation.PostConstruct;
import java.util.Optional;
import org.springframework.cache.annotation.Cacheable;

/**
 * AbstractConfigCacheService class
 * @param <T> entity class
 */
public abstract  class AbstractConfigCacheService<T> {

    /**
     * Load from database by key.
     * @param operationName operation
     * @return system config db entity
     */
    protected abstract Optional<T> loadFromDatabase(String operationName);

    /**
     * Extract actual value from the database entity.
     * @param entity system config db entity
     * @return operation endpoint url
     */
    protected abstract String extractValue(T entity);

    /**
     * load the cache
     */
    @PostConstruct
    public void initCache() {
        preload();  // delegate preload to subclass
    }

    /**
     * preload the cache during runtime
     */
    protected abstract void preload();

    /**
     * Exposed method to get config value (with cache).
     * @param operationName operation
     * @return operation endpoint url
     */
    @Cacheable(value = "soapAdapterSysConfigCache")
    public String getConfig(final String operationName) {
        return loadFromDatabase(operationName)
                .map(this::extractValue)
                .orElse(null);
    }
}
