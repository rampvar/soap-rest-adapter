package com.soaprestadapter.cache;

import com.soaprestadapter.DatabaseService;
import com.soaprestadapter.entity.SystemConfigDataEntity;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;

/**
 * CaffeineCacheStrategy implementation
 */
@Service("caffeine")
@ConditionalOnProperty(name = "app.cache.type", havingValue = "caffeine")
@RequiredArgsConstructor
public class CaffeineCacheSystemConfigStrategy extends
        AbstractConfigCacheService<SystemConfigDataEntity> implements ConfigCacheStrategy {

    /**
     * SystemConfigDataRepository
     */
    private final DatabaseService databaseService;

    /**
     * @param operationName operation name
     * @return SystemConfigDataEntity
     */
    @Override
    protected Optional<SystemConfigDataEntity> loadFromDatabase
    (final String operationName) {
        return databaseService.findByOperationName(operationName);
    }

    /**
     * @param config operation config in db
     * @return operation endpoint url
     */
    @Override
    protected String extractValue(final SystemConfigDataEntity config) {
        return config.getOperationEndpointUrl();
    }

    /**
     * load the db entries to cache
     */
    @Override
    public void preload() {
        List<SystemConfigDataEntity> allConfigs = databaseService.findAllForSystemConfig();
        allConfigs.forEach(config ->
                getConfig(config.getOperationName()) // this triggers caching
        );
    }
}
