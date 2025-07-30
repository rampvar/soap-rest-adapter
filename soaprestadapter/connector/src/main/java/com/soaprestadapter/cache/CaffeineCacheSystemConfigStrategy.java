package com.soaprestadapter.cache;

import com.soaprestadapter.Repository.SystemConfigDataRepository;
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
    private final SystemConfigDataRepository repository;

    /**
     * @param operationName operation name
     * @return SystemConfigDataEntity
     */
    @Override
    protected Optional<SystemConfigDataEntity> loadFromDatabase
    (final String operationName) {
        return Optional.ofNullable(repository.findByOperationName(operationName));
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
        List<SystemConfigDataEntity> allConfigs = repository.findAll();
        allConfigs.forEach(config ->
                getConfig(config.getOperationName()) // this triggers caching
        );
    }
}
