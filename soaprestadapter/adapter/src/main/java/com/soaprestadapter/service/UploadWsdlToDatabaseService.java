package com.soaprestadapter.service;

import java.nio.file.Path;
import java.util.List;

/**
 *  This interface represents the UploadWsdlToDatabaseService.
 */
public interface UploadWsdlToDatabaseService {

    /**
     *  Uploads the WSDL files from the provided URL to the database.
     * @param wsdlUrl
     * @param filesPath
     */
    void uploadWsdlToDb(final String wsdlUrl, final List<Path> filesPath);
}
