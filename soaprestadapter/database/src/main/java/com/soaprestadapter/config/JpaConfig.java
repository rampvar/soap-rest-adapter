package com.soaprestadapter.config;

import jakarta.annotation.PostConstruct;
import jakarta.persistence.EntityManagerFactory;
import java.util.Arrays;
import javax.sql.DataSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.boot.orm.jpa.EntityManagerFactoryBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.core.env.Environment;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.transaction.PlatformTransactionManager;


@Configuration
@Profile("!sqlite")
@EnableJpaRepositories(basePackages = "com.soaprestadapter.Repository")
@EntityScan(basePackages = "com.soaprestadapter.entity")
public class JpaConfig {

    /**
     * logger initilization
     */
    private static final Logger LOGGER = LoggerFactory.getLogger(JpaConfig.class);

    /**
     * Autowire environment to get profile to display
     */
    @Autowired
    private Environment environment;

    /**
     * print active db profile
     */
    @PostConstruct
    public void init() {
        LOGGER.info("Active Spring Profiles DB: {}", Arrays.toString(environment.getActiveProfiles()));
    }

    @Bean
    public LocalContainerEntityManagerFactoryBean entityManagerFactory(
            final EntityManagerFactoryBuilder builder, final DataSource dataSource) {


        return builder
                .dataSource(dataSource)
                .packages("com.soaprestadapter.entity")
                .persistenceUnit("default")
                .build();
    }


    @Bean
    public PlatformTransactionManager transactionManager(final EntityManagerFactory emf) {
        return new JpaTransactionManager(emf);
    }

}
