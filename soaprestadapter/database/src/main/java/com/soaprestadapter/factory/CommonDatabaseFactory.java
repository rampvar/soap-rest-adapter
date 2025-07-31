package com.soaprestadapter.factory;

import com.soaprestadapter.properties.DatabaseProperties;
import com.zaxxer.hikari.HikariDataSource;
import jakarta.persistence.EntityManagerFactory;
import javax.sql.DataSource;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.orm.jpa.vendor.HibernateJpaVendorAdapter;
import org.springframework.stereotype.Component;
import org.springframework.transaction.PlatformTransactionManager;

/**
 *
 */
@Slf4j
@RequiredArgsConstructor
@Component
public class CommonDatabaseFactory implements DatabaseFactory {
    /**
     * Database Properties class
     */
    private final DatabaseProperties databaseProperties;

    /**
     *
     * @return DataSource
     */
    public DataSource createDataSource() {
        HikariDataSource ds = new HikariDataSource();
        ds.setJdbcUrl(databaseProperties.getUrl());
        ds.setUsername(databaseProperties.getUsername());
        ds.setPassword(databaseProperties.getPassword());
        ds.setDriverClassName(databaseProperties.getDriverClassName());
        return ds;
    }


    /**
     *
     * @param ds dataSource
     * @return LocalContainerEntityManagerFactoryBean
     */
    @Override
    public LocalContainerEntityManagerFactoryBean createEntityManagerFactory(final DataSource ds) {
        LocalContainerEntityManagerFactoryBean emf = new LocalContainerEntityManagerFactoryBean();
        emf.setDataSource(createDataSource());
        emf.setPackagesToScan("com.soaprestadapter.entity");
        emf.setJpaVendorAdapter(new HibernateJpaVendorAdapter());
        return emf;
    }

    /**
     *
     * @param emf
     * @return PlatformTransactionManager
     */
    @Override
    public PlatformTransactionManager createTransactionManager(final EntityManagerFactory emf) {
        return new JpaTransactionManager(emf);
    }
}
