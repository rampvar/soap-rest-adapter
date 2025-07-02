package com.soaprestadapter.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Lob;
import jakarta.persistence.Table;
import java.time.LocalDateTime;
import lombok.Data;

/**
 * Entity class representing the generated WSDL class data.
 */
@Entity
@Table(name = "tbl_generated_wsdl_classes")
@Data
public class GeneratedWsdlClassEntity {

    /**
     * Unique identifier for the class record.
     * -- GETTER --
     *  Get Id Column
     *
     *
     * -- SETTER --
     *  Set Id Column
     *
     @return Long representing the unique identifier for the class record.
      * @param classId

     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * The WSDL URL associated with the generated class data.
     * -- GETTER --
     *  Get WsdlUrl Column
     *
     *
     * -- SETTER --
     *  Set WsdlUrl Column
     *
     @return  String representing the WSDL URL associated with the generated class data.
      * @param url

     */
    @Column(name = "wsdl_url", nullable = false)
    private String wsdlUrl;

    /**
     * The byte array containing the compiled .class file data.
     * -- GETTER --
     *  Get ClassData Column
     *
     *
     * -- SETTER --
     *  Set ClassData Column
     *
     @return  byte array containing the compiled .class file data.
      * @param data

     */
    @Lob
    @Column(name = "class_data", nullable = false)
    private byte[] classData;

    /**
     * The timestamp indicating when the class data was generated.
     * -- GETTER --
     *  Get GeneratedAt Column
     *
     *
     * -- SETTER --
     *   Set GeneratedAt Column
     *
     @return LocalDateTime object representing the timestamp when the class data was generated.
      * @param generatedTime

     */
    @Column(name = "generated_at", nullable = false)
    private LocalDateTime generatedAt;

}
