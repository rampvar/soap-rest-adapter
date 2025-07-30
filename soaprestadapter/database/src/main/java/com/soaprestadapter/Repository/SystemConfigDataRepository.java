package com.soaprestadapter.Repository;

import com.soaprestadapter.entity.SystemConfigDataEntity;
import org.springframework.context.annotation.Profile;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * Repository for managing {@link SystemConfigDataEntity} entities.
 */
@Repository
@Profile("!sqlite")
public interface SystemConfigDataRepository extends JpaRepository<SystemConfigDataEntity, Long> {
    /**
     * Repository for managing entities.
     * @param operationName the name of the operation
     * @return the entity with the given operation name, or null if not found.
     */
    SystemConfigDataEntity findByOperationName(String operationName);
}
