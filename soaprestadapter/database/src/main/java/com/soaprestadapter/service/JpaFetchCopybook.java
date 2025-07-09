package com.soaprestadapter.service;

import com.soaprestadapter.FetchResponseCopybookDataStrategy;
import com.soaprestadapter.Repository.FetchResponseCopybookDataRepository;
import com.soaprestadapter.entity.FetchResponseCopybookDataEntity;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;

/**
 * Service class for JPA Storage for other than sqlite db.
 */
@Service
@Qualifier("jpaWsdlStorage")
@Profile("!sqlite")
@RequiredArgsConstructor
public class JpaFetchCopybook implements FetchResponseCopybookDataStrategy {
    /**
     *  Inject repository class
     */
    private final FetchResponseCopybookDataRepository fetchResponseCopybookDataRepository;


    /**
     * Method to save WsdlClassEntity to Jpa storage
     * @param fetchResponseCopybookDataEntity entity to be saved
     */
    @Override
    public void save(final FetchResponseCopybookDataEntity fetchResponseCopybookDataEntity) {

        fetchResponseCopybookDataRepository.save(fetchResponseCopybookDataEntity);
    }

    /**
     * Method to find all WsdlClassEntity from Jpa storage
     * @return list of rows
     */
    @Override
    public List<FetchResponseCopybookDataEntity> findAll() {

        return fetchResponseCopybookDataRepository.findAll();
    }

    @Override
    public FetchResponseCopybookDataEntity getByOperationName(final String operationName) {
        return fetchResponseCopybookDataRepository.findByOperationName(operationName);

    }


}
