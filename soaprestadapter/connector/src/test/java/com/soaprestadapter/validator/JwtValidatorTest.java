package com.soaprestadapter.validator;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import org.junit.jupiter.api.Test;
import java.time.Instant;
import java.time.temporal.ChronoUnit;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

public class JwtValidatorTest {

    @Test
    void shouldValidateTokenWhenItIsValidJwtWithExpiryDateInFuture() {
        String validJwtToken = generateValidJwtToken();

        assertDoesNotThrow(() -> JwtValidator.validateToken(validJwtToken));
    }

    private String generateValidJwtToken() {
        Instant now = Instant.now();
        Instant expiresAt = now.plus(10, ChronoUnit.MINUTES);

        return JWT.create()
                .withSubject("user123")
                .withIssuer("my-app")
                .withIssuedAt(now)
                .withExpiresAt(expiresAt)
                .withClaim("role", "ADMIN")
                .sign(Algorithm.HMAC256("my-secret-key"));
    }
}