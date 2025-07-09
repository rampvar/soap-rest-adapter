package com.soaprestadapter.service;

import com.soaprestadapter.FetchResponseCopybookDataStrategy;
import com.soaprestadapter.factory.ResponseHandler;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/**
 * class for handling Blueage REST responses.
 */
@Slf4j
@Component("BLUEAGE-RESPONSE")
@RequiredArgsConstructor
public class BlueageResponseHandler implements ResponseHandler {

    /**
     * repository to get response copybook data
     */
    private  final FetchResponseCopybookDataStrategy repository;

    /**
     * convert rest response to Blueage format
     * @param responseBody - rest response body
     * @return - converted blueage format json string
     */
    @Override
    public String convertRestResponse
    (final String responseBody, final String operationName) {
        log.info("Converting rest response");
        // Perform custom conversion logic here
        return responseBody;
    }

}
