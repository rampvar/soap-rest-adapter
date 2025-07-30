package com.soaprestadapter.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;

/**
 * Entity class representing the system config data.
 */
@Entity
@Table(name = "soap_adapter_system_config")
@Data
public class SystemConfigDataEntity {

    /**
     * Unique identifier for the class record.
     * -- GETTER --
     *  Get Id Column
     * -- SETTER --
     *  Set Id Column
     *
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * The ConnectorType associated with the application.
     * -- GETTER --
     *  Get ConnectorType Column
     * -- SETTER --
     *  Set ConnectorType Column
     *
     */
    @Column(name = "connector_type", nullable = false)
    private String connectorType;

    /**
     * The string contains operationName data.
     * -- GETTER --
     *  Get operationName Column
     *
     *
     * -- SETTER --
     *  Set operationName Column
     *
     */
    @Column(name = "operation_name", nullable = false)
    private String operationName;

    /**
     * The wsdlName indicating wsdl service.
     * -- GETTER --
     *  Get wsdlName Column
     *
     *
     * -- SETTER --
     *   Set wsdlName Column
     *
     */
    @Column(name = "wsdl_name", nullable = false)
    private String wsdlName;

    /**
     * The operationEndpointUrl indicating operation rest endpoint url.
     * -- GETTER --
     *  Get operationEndpointUrl Column
     *
     *
     * -- SETTER --
     *   Set operationEndpointUrl Column
     *
     */
    @Column(name = "operation_endpoint_url", nullable = false)
    private String operationEndpointUrl;

    /**
     * The environment indicating env of application.
     * -- GETTER --
     *  Get environment Column
     *
     *
     * -- SETTER --
     *   Set environment Column
     *
     */
    @Column(name = "environment", nullable = false)
    private String environment;

}
