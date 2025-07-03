package com.soaprestadapter.config;

import javax.sql.DataSource;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.jdbc.core.JdbcTemplate;

/**
 * Configuration for SQLite database
 */
@Configuration
@Profile("sqlite")
@RequiredArgsConstructor
public class SqliteConfig {

    /**
     * Datasource to be used in sqlite configuration
     */
    private final DataSource dataSource;

    /**
     * JdbcTemplate bean to interact with SQLite database
     * @return JdbcTemplate bean
     */
    @Bean
    public JdbcTemplate jdbcTemplate() {
        return new JdbcTemplate(dataSource);
    }
}
