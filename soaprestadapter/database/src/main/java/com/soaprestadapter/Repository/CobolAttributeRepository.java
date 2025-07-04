package com.soaprestadapter.Repository;

import com.soaprestadapter.entity.CobolAttributeEntity;
import org.springframework.context.annotation.Profile;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
@Profile("!sqlite")
public interface CobolAttributeRepository extends JpaRepository<CobolAttributeEntity, String> {

    @Query(value = "SELECT Request_payload1 " +
            "FROM mydb.cobol_fixed_length_attributes " +
            "WHERE operationName = :operationName", nativeQuery = true)
    String findPayloadOneByOperationName(@Param("operationName") String operationName);

    @Query(value = "SELECT Request_payload2 " +
            "FROM mydb.cobol_fixed_length_attributes " +
            "WHERE operationName = :operationName", nativeQuery = true)
    String findPayloadTwoByOperationName(@Param("operationName") String operationName);


}
