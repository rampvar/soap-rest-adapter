package com.soaprestadapter.service;

import com.soaprestadapter.config.ServiceUrlConfig;
import com.soaprestadapter.validator.JwtValidator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

/**
 * RestClientService class
 */
@RequiredArgsConstructor
@Service
@Slf4j
public class RestClientService {


    /**
     * jwtTokenRequired
     */
    @Value("${auth.jwt_token_required}")
    /**
     * jwtTokenRequired
     */
    private boolean jwtTokenRequired;
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
     * @param jwt
     *
     * @return ResponseEntity<String>
     */
    public ResponseEntity<String> process(final String connectorName,
                                          final String operationName,
                                          final  String payload,
                                          final String jwt) {

        log.info("Rest Payload {}", payload);
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        if (jwtTokenRequired && StringUtils.isNotBlank(jwt)) {
            JwtValidator.validateToken(jwt);
            headers.set("Authorization", "Bearer " + jwt);
        }
        HttpEntity<String> request = new HttpEntity<>(payload, headers);
        String url = serviceUrlConfig.getUrl(connectorName, operationName);

        return restTemplate.exchange(url,
                HttpMethod.POST,
                request,
                String.class
        );

    }
}