package com.soaprestadapter.service;

import com.soaprestadapter.config.ServiceUrlConfig;
import lombok.RequiredArgsConstructor;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@RequiredArgsConstructor
@Service
public class RestClientService {

    private RestTemplate restTemplate= new RestTemplate();
    private final ServiceUrlConfig serviceUrlConfig;


    public ResponseEntity<String> process(String connectorName, String operationName, String payload) {

        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            HttpEntity<String> request = new HttpEntity<>(payload, headers);
            String url = serviceUrlConfig.getUrl(connectorName, operationName);

            return restTemplate.exchange(url,
                    HttpMethod.POST,
                    request,
                    String.class
            );
        } catch (Exception e) {
            throw new RuntimeException("Failed to send request for operation"+operationName, e);
        }
    }
}