package com.soaprestadapter.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;

/**
 * Entity class representing the generated response copybook data.
 */
@Entity
@Table(name = "tbl_response_copybook_data")
@Data
public class FetchResponseCopybookDataEntity {

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
     * The operationName associated with the generated copybook data.
     * -- GETTER --
     *  Get operationName Column
     * -- SETTER --
     *  Set operationName Column
     *
     */
    @Column(name = "operation_name", nullable = false)
    private String operationName;

    /**
     * The string contains RequestHeader data.
     * -- GETTER --
     *  Get RequestHeader Column
     *
     *
     * -- SETTER --
     *  Set RequestHeader Column
     *
     */
    @Column(name = "request_header", nullable = false)
    private String requestHeader;

    /**
     * The ResponseAttributes indicating response copybook data generated.
     * -- GETTER --
     *  Get ResponseAttributes Column
     *
     *
     * -- SETTER --
     *   Set ResponseAttributes Column
     *
     */
    @Column(name = "response_attributes", nullable = false)
    private String responseAttributes;

}
