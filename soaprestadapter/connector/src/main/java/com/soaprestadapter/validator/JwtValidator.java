package com.soaprestadapter.validator;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.Base64;
import java.util.Date;
import java.util.Map;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;


/**
 * JwtValidator
 */
@Slf4j
@Component
public class JwtValidator {

    /**
     * SECONDS_TO_MILLIS constant */
    private static final int SECONDS_TO_MILLIS = 1000;

    /**
     * THREE constant
     * */
    private static final int THREE = 3;

    /**
     * method to validate JWT token
     *
     * @param token JWT token
     * @return true if token is valid, false otherwise
     */
    public static boolean validateToken(final String token) {

        String[] parts = token.split("\\.");
        if (parts.length != THREE) {
            throw new IllegalArgumentException("Invalid JWT token format");
        }

        // Decode payload (middle part)
        String payloadJson = new String(Base64.getUrlDecoder().decode(parts[1]));

        // Parse JSON to Map
        ObjectMapper mapper = new ObjectMapper();
        Map<String, Object> claims = null;
        try {
            claims = mapper.readValue(payloadJson, Map.class);
        } catch (JsonProcessingException e) {
            throw new RuntimeException("Failed processing claims");
        }

        Object expClaim = claims.get("exp");
        if (expClaim == null) {
            log.info("No 'exp' claim in token");
        }

        long expSeconds = ((Number) expClaim).longValue();
        long currentSeconds = System.currentTimeMillis() / SECONDS_TO_MILLIS;

        if (currentSeconds > expSeconds) {
            log.info("Token expired at: " + new Date(expSeconds * SECONDS_TO_MILLIS));
            return false;
        } else {
            log.info("Token valid. Expires at: " + new Date(expSeconds * SECONDS_TO_MILLIS));
            return true;
        }

    }

}
