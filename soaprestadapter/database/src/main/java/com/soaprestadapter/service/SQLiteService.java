package com.soaprestadapter.service;

import com.soaprestadapter.WsdlToClassStorageStrategy;
import com.soaprestadapter.entity.GeneratedWsdlClassEntity;
import jakarta.annotation.PostConstruct;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Profile;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Service;

/**
 * SQLliteService class implements WsdlToClassStorageStrategy interface for Sqlite DB.
 */
@Service
@Qualifier("sqliteStorage")
@Profile("sqlite")
@RequiredArgsConstructor
public class SQLiteService implements WsdlToClassStorageStrategy {

    /**
     * Jdbc Template declaration used for SQLlite DB
     */
    private final JdbcTemplate jdbcTemplate;


    /**
     * Creating table if not exists in SQLlite DB
     */
    @PostConstruct
    public void createTable() {
        jdbcTemplate.execute("CREATE TABLE IF NOT EXISTS tbl_generated_wsdl_classes (\n" +
                "    id INTEGER PRIMARY KEY AUTOINCREMENT,\n" +
                "    wsdl_url TEXT NOT NULL,\n" +
                "    class_data BLOB NOT NULL,\n" +
                "    generated_at TEXT NOT NULL \n" +
                ");");
    }

    /**
     * Saving generated WSDL class to SQLlite DB
     * @param wsdlClassEntity
     */
    @Override
    public void save(final GeneratedWsdlClassEntity wsdlClassEntity) {

        String sql = "  INSERT INTO tbl_generated_wsdl_classes (wsdl_url, class_data, generated_at)\n" +
                " VALUES (?, ?, ?)";

        jdbcTemplate.update(
                sql,
                wsdlClassEntity.getWsdlUrl(),
                wsdlClassEntity.getClassData(),
                wsdlClassEntity.getGeneratedAt().toString()
        );
    }

    /**
     * Finding all generated WSDL classes from SQLlite DB
     * @return
     */
    @Override
    public List<GeneratedWsdlClassEntity> findAll() {
        String sql = "SELECT id, wsdl_url, class_data, generated_at FROM tbl_generated_wsdl_classes";

        return jdbcTemplate.query(sql, new RowMapper<GeneratedWsdlClassEntity>() {
            @Override
            public GeneratedWsdlClassEntity mapRow(final ResultSet result, final int rowNum) throws SQLException {
                GeneratedWsdlClassEntity entity = new GeneratedWsdlClassEntity();
                entity.setId(result.getLong("id"));
                entity.setWsdlUrl(result.getString("wsdl_url"));
                entity.setClassData(result.getBytes("class_data"));
                entity.setGeneratedAt(LocalDateTime.parse(result.getString("generated_at")));
                return entity;
            }
        });
    }

    @Override
    public String findPayloadOneByOperationName(String operationName) {
        return null;
    }

    @Override
    public String findPayloadTwoByOperationName(String operationName) {
        return null;
    }
}

