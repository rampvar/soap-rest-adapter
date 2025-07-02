package com.soaprestadapter.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Lob;
import jakarta.persistence.Table;
import java.time.LocalDateTime;


@Entity
@Table(name = "tbl_generated_wsdl_classes")
public class GeneratedWsdlClassEntity {

    /**
     * Unique identifier for the class record.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * The WSDL URL associated with the generated class data.
     */
    @Column(name = "wsdl_url", nullable = false)
    private String wsdlUrl;

    /**
     * The byte array containing the compiled .class file data.
     */
    @Lob
    @Column(name = "class_data", nullable = false)
    private byte[] classData;

    /**
     * The timestamp indicating when the class data was generated.
     */
    @Column(name = "generated_at", nullable = false)
    private LocalDateTime generatedAt;

    public Long getId() {
        return id;
    }

    public void setId(final Long id) {
        this.id = id;
    }

    public String getWsdlUrl() {
        return wsdlUrl;
    }

    public void setWsdlUrl(final String wsdlUrl) {
        this.wsdlUrl = wsdlUrl;
    }

    public byte[] getClassData() {
        return classData;
    }

    public void setClassData(final byte[] classData) {
        this.classData = classData;
    }

    public LocalDateTime getGeneratedAt() {
        return generatedAt;
    }

    public void setGeneratedAt(final LocalDateTime generatedAt) {
        this.generatedAt = generatedAt;
    }
}
