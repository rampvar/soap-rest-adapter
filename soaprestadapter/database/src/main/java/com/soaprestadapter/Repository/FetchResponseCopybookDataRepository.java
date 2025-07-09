package com.soaprestadapter.Repository;

import com.soaprestadapter.entity.FetchResponseCopybookDataEntity;
import org.springframework.context.annotation.Profile;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * Repository for managing {@link FetchResponseCopybookDataEntity} entities.
 */
@Repository
@Profile("!sqlite")
public interface FetchResponseCopybookDataRepository extends JpaRepository<FetchResponseCopybookDataEntity, Long> {
    /**
     * Repository for managing entities.
     * @param operationName the name of the operation
     * @return the entity with the given operation name, or null if not found.
     */
    FetchResponseCopybookDataEntity findByOperationName(String operationName);
}
