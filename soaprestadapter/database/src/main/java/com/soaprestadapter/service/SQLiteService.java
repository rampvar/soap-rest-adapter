package com.soaprestadapter.service;

import com.soaprestadapter.DatabaseService;
import com.soaprestadapter.entity.FetchResponseCopybookDataEntity;
import com.soaprestadapter.entity.GeneratedWsdlClassEntity;
import com.soaprestadapter.entity.SystemConfigDataEntity;
import jakarta.annotation.PostConstruct;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Profile;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Service;

/**
 * SQLliteService class implements WsdlToClassStorageStrategy interface for Sqlite DB.
 */
@Service
@Profile("sqlite")
@RequiredArgsConstructor
public class SQLiteService implements DatabaseService {

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

    /**
     * findPayloadOneByOperationName from SQLlite DB
     * @return
     */
    @Override
    public String findPayloadOneByOperationName(final String operationName) {

        String sql = """
                    SELECT request_payload1
                    FROM cobol_fixed_length_attributes
                    WHERE operation_name = ?
                """;

        List<String> result = jdbcTemplate.query(sql, new Object[]{operationName},
                (rs, rowNum) -> rs.getString("request_payload1"));

        return result.stream().findFirst().orElse(null);
    }

    /**
     * findPayloadTwoByOperationName from SQLlite DB
     * @return
     */
    @Override
    public String findPayloadTwoByOperationName(final String operationName) {

        String sql = """
                    SELECT request_payload2
                    FROM cobol_fixed_length_attributes
                    WHERE operation_name = ?
                """;

        List<String> result = jdbcTemplate.query(sql, new Object[]{operationName},
                (rs, rowNum) -> rs.getString("request_payload2"));

        return result.stream().findFirst().orElse(null);
    }

    /**
     * Method to retrieve entity based on wsdl url
     * @param wsdlUrl as input
     * @return GeneratedWsdlClassEntity
     */
    @Override
    public Optional<GeneratedWsdlClassEntity> findByWsdlUrl(final String wsdlUrl) {
        String sql = "SELECT id, wsdl_url, class_data, generated_at FROM tbl_generated_wsdl_classes WHERE wsdl_url = ?";

        List<GeneratedWsdlClassEntity> result = jdbcTemplate.query(sql, new RowMapper<GeneratedWsdlClassEntity>() {
            @Override
            public GeneratedWsdlClassEntity mapRow(final ResultSet rs, final int rowNum) throws SQLException {
                GeneratedWsdlClassEntity entity = new GeneratedWsdlClassEntity();
                entity.setId(rs.getLong("id"));
                entity.setWsdlUrl(rs.getString("wsdl_url"));
                entity.setClassData(rs.getBytes("class_data"));
                entity.setGeneratedAt(LocalDateTime.parse(rs.getString("generated_at")));
                return entity;
            }
        }, wsdlUrl);

        return result.stream().findFirst();
    }

    /**
     * getByOperationName
     * @param operationName to fetch data for  from the db
     * @return FetchResponseCopybookDataEntity
     */
    public Optional<FetchResponseCopybookDataEntity> getByOperationName(final String operationName) {
        String sql = "SELECT id, operation_name, request_header, response_attributes " +
                "FROM tbl_response_copybook_data WHERE operation_name = ?";

        List<FetchResponseCopybookDataEntity> result = jdbcTemplate.query(sql, (rs, rowNum) -> {
            FetchResponseCopybookDataEntity entity = new FetchResponseCopybookDataEntity();
            entity.setId(rs.getLong("id"));
            entity.setOperationName(rs.getString("operation_name"));
            entity.setRequestHeader(rs.getString("request_header"));
            entity.setResponseAttributes(rs.getString("response_attributes"));
            return entity;
        }, operationName);

        return result.stream().findFirst();
    }

    /**
     * Get no of entity in db
     * @ count value
     */
    @Override
    public Optional<Long> getEntityCount() {
        return Optional.ofNullable(jdbcTemplate.queryForObject(
                "SELECT COUNT(*) FROM tbl_generated_wsdl_classes", Long.class));
    }

    /**
     * entitle user
     * @param userId
     * @return boolean value
     */
    @Override
    public boolean isUserEntitled(final Long userId) {
        String sql = """
                SELECT COUNT(*) > 0
                FROM tbl_user_role ur
                JOIN tbl_role_group rg ON ur.role_id = rg.role_id
                JOIN tbl_user_group ug ON rg.user_group_id = ug.user_group_id
                WHERE ur.user_id = ?
                  AND ug.is_authorized = 1
                """;

        return jdbcTemplate.queryForObject(sql, Boolean.class, userId);
    }

    /**
     * @param operationName to fetch data for from the db
     * @return SystemConfigDataEntity
     */
    @Override
    public Optional<SystemConfigDataEntity> findByOperationName(final String operationName) {
        String sql = "SELECT id, connector_type, operation_name, wsdl_name, operation_endpoint_url, environment " +
                "FROM soap_adapter_system_config WHERE operation_name = ?";

        List<SystemConfigDataEntity> result = jdbcTemplate.query(sql, new RowMapper<SystemConfigDataEntity>() {
            @Override
            public SystemConfigDataEntity mapRow(final ResultSet rs, final int rowNum) throws SQLException {
                SystemConfigDataEntity entity = new SystemConfigDataEntity();
                entity.setId(rs.getLong("id"));
                entity.setConnectorType(rs.getString("connector_type"));
                entity.setOperationName(rs.getString("operation_name"));
                entity.setWsdlName(rs.getString("wsdl_name"));
                entity.setOperationEndpointUrl(rs.getString("operation_endpoint_url"));
                entity.setEnvironment(rs.getString("environment"));
                return entity;
            }
        }, operationName);

        return result.stream().findFirst();
    }

    /**
     *
     * @return SystemConfigDataEntity list
     */
    @Override
    public List<SystemConfigDataEntity> findAllForSystemConfig() {
        String sql = "SELECT id, connector_type, operation_name, wsdl_name, operation_endpoint_url, environment " +
                "FROM soap_adapter_system_config";

        return jdbcTemplate.query(sql, new RowMapper<SystemConfigDataEntity>() {
            @Override
            public SystemConfigDataEntity mapRow(final ResultSet rs, final int rowNum) throws SQLException {
                SystemConfigDataEntity entity = new SystemConfigDataEntity();
                entity.setId(rs.getLong("id"));
                entity.setConnectorType(rs.getString("connector_type"));
                entity.setOperationName(rs.getString("operation_name"));
                entity.setWsdlName(rs.getString("wsdl_name"));
                entity.setOperationEndpointUrl(rs.getString("operation_endpoint_url"));
                entity.setEnvironment(rs.getString("environment"));
                return entity;
            }
        });
    }
}

