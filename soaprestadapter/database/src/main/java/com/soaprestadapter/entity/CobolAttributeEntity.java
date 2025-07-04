package com.soaprestadapter.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;

@Entity
@Table(name = "cobol_fixed_length_attributes")
@Data
public class CobolAttributeEntity {

    @Id
    @Column(name = "OperationName")
    private String operationName;

    @Column(name = "ProgramName")
    private String programName;

    @Column(name = "Request_payload1")
    private String request_payload1;

    @Column(name = "Request_payload2")
    private String request_payload2;

    @Column(name = "Request_payload3")
    private String request_payload3;
}
