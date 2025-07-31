package com.soaprestadapter.config;

import com.soaprestadapter.factory.CommonDatabaseFactory;
import com.soaprestadapter.properties.DatabaseProperties;
import jakarta.annotation.PostConstruct;
import jakarta.persistence.EntityManagerFactory;
import java.util.Arrays;
import javax.sql.DataSource;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.core.env.Environment;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.transaction.PlatformTransactionManager;


/**
 *
 */
@Configuration
@EnableConfigurationProperties(DatabaseProperties.class)
@Profile("!sqlite")
@EnableJpaRepositories(basePackages = "com.soaprestadapter.Repository")
@EntityScan(basePackages = "com.soaprestadapter.entity")
@RequiredArgsConstructor
@Slf4j
public class DataSourceConfig {

    /**
     * Autowire environment to get profile to display
     */
    private final Environment environment;

    /**
     *
     */
    private final CommonDatabaseFactory databaseFactory;



    /**
     * print active db profile
     */
    @PostConstruct
    public void init() {
        log.info("Active Spring Profiles DB: {}", Arrays.toString(environment.getActiveProfiles()));
    }


    /**
     *
     * @return DataSource
     */
    @Bean
    public DataSource dataSource() {
        return databaseFactory.createDataSource();
    }

    /**
     *
     * @param dataSource
     * @return LocalContainerEntityManagerFactoryBean
     */
    @Bean
    public LocalContainerEntityManagerFactoryBean entityManagerFactory(final DataSource dataSource) {
        return databaseFactory.createEntityManagerFactory(dataSource);
    }

    /**
     *
     * @param emf
     * @return PlatformTransactionManager
     */
    @Bean
    public PlatformTransactionManager transactionManager(final EntityManagerFactory emf) {
        return databaseFactory.createTransactionManager(emf);
    }

}
