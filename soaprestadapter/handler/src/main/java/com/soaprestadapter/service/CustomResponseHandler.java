package com.soaprestadapter.service;

import com.soaprestadapter.FetchResponseCopybookDataStrategy;
import com.soaprestadapter.factory.ResponseHandler;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/**
 * Custom response handler implementation.
 * This class can be extended to handle custom response conversions.
 */
@Slf4j
@Component("CUSTOM-RESPONSE")
@RequiredArgsConstructor
public class CustomResponseHandler implements ResponseHandler {

    /**
     * repository to get response copybook data
     */
    private  final FetchResponseCopybookDataStrategy repository;

    /**
     * convert rest response to custom format
     * @param responseBody - rest response body
     * @return - converted custom format json string
     */
    @Override
    public String convertRestResponse(final String responseBody, final String operationName) {
        log.info("Converting rest response");
        // Perform custom conversion logic here
        return responseBody;
    }
}
