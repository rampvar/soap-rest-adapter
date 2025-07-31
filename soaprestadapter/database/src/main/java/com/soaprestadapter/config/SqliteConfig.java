package com.soaprestadapter.config;

import com.soaprestadapter.factory.CommonDatabaseFactory;
import com.soaprestadapter.properties.DatabaseProperties;
import javax.sql.DataSource;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.jdbc.core.JdbcTemplate;

/**
 *  Configuration for SQLite database
 */
@Configuration
@EnableConfigurationProperties(DatabaseProperties.class)
@Profile("sqlite")
@RequiredArgsConstructor
@Slf4j
public class SqliteConfig {

    /**
     *
     */
    private final CommonDatabaseFactory databaseFactory;

    /**
     *
     * @return DataSource
     */
    @Bean
    public DataSource dataSource() {
        return databaseFactory.createDataSource();
    }

    /**
     * JdbcTemplate bean to interact with SQLite database
     *     @return JdbcTemplate bean
     */
    @Bean
    public JdbcTemplate jdbcTemplate() {
        return new JdbcTemplate(dataSource());
    }
}
