package com.soaprestadapter.config;

import javax.sql.DataSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.jdbc.core.JdbcTemplate;

/**
 * Configuration for SQLite database
 */
@Configuration
@Profile("sqlite")
public class SqliteConfig {

    /**
     * Datasource to be used in sqlite configuration
     */
    @Autowired
    private DataSource dataSource;

    /**
     * JdbcTemplate bean to interact with SQLite database
     * @return JdbcTemplate bean
     */
    @Bean
    public JdbcTemplate jdbcTemplate() {
        return new JdbcTemplate(dataSource);
    }
}
