package com.soaprestadapter.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;


/**
 * Entity class representing the Cobol attributes class data.
 */
@Entity
@Table(name = "cobol_fixed_length_attributes")
@Data
public class CobolAttributeEntity {

    /**
     * OperationName column
     */
    @Id
    @Column(name = "OperationName")
    private String operationName;

    /**
     * ProgramName column
     */
    @Column(name = "ProgramName")
    private String programName;

    /**
     * Request_payload1 column
     */
    @Column(name = "Request_payload1")
    private String request_payload1;

    /**
     * Request_payload2 column
     */
    @Column(name = "Request_payload2")
    private String request_payload2;

    /**
     * Request_payload3 column
     */
    @Column(name = "Request_payload3")
    private String request_payload3;
}
