package com.soaprestadapter.cache;

import jakarta.annotation.PostConstruct;
import java.util.Map;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

/**
 * CacheDispatcher class
 */
@Service
public class CacheDispatcher {

    /**
     * ConfigCacheStrategy specified
     */
    private final ConfigCacheStrategy cacheStrategy;

    /**
     * @param cacheType mechanism specified
     * @param strategies available stratergies
     */
    public CacheDispatcher(@Value("${app.cache.type}") final String cacheType,
                           final Map<String, ConfigCacheStrategy> strategies) {
        this.cacheStrategy = strategies.get(cacheType);
        if (this.cacheStrategy == null) {
            throw new IllegalArgumentException("Invalid cache type: " + cacheType);
        }
    }

    /**
     * @param op operationName
     * @return operation endpoint url
     */
    public String getConfig(final String op) {
        return cacheStrategy.getConfig(op);
    }

    /**
     * preload cache at runtime
     */
    @PostConstruct
    public void preloadAll() {
        cacheStrategy.preload();
    }
}
