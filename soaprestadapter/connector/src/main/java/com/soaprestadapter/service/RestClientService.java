package com.soaprestadapter.service;

import com.soaprestadapter.config.ServiceUrlConfig;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpEntity;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

/**
 * RestClientService class
 */
@RequiredArgsConstructor
@Service
public class RestClientService {

    /**
     * RestTemplate
     */
    private RestTemplate restTemplate = new RestTemplate();

    /**
     * ServiceUrlConfig
     */
    private final ServiceUrlConfig serviceUrlConfig;


    /**
     * process method
     *
     * @param connectorName
     * @param operationName
     * @param payload
     * @return ResponseEntity<String>
     */
    public ResponseEntity<String> process(final String connectorName,
                                          final String operationName, final  String payload) {

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<String> request = new HttpEntity<>(payload, headers);
        String url = serviceUrlConfig.getUrl(connectorName, operationName);

        return restTemplate.exchange(url,
                HttpMethod.POST,
                request,
                String.class
        );

    }
}