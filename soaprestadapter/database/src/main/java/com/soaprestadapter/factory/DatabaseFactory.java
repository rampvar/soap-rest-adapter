package com.soaprestadapter.factory;

import jakarta.persistence.EntityManagerFactory;
import javax.sql.DataSource;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.transaction.PlatformTransactionManager;


/**
 * Database Factory Interface
 */
public interface DatabaseFactory {

    /**
     * create datasource
     * @return DataSource
     */
    DataSource createDataSource();

    /**
     *
     * @param dataSource
     * @return LocalContainerEntityManagerFactoryBean
     */
    LocalContainerEntityManagerFactoryBean createEntityManagerFactory(DataSource dataSource);

    /**
     *
     * @param emf
     * @return PlatformTransactionManager
     */
    PlatformTransactionManager createTransactionManager(EntityManagerFactory emf);

}
