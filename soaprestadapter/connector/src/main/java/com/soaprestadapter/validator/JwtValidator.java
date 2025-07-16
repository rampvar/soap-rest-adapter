package com.soaprestadapter.validator;

import com.auth0.jwt.JWT;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.soaprestadapter.exception.JwtValidationException;
import java.time.Instant;
import java.util.Date;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;


/**
 * Validates a JWT token using the Auth0 Java JWT library.
 */
@Slf4j
@Component
public class JwtValidator {

    /**
     * Validates the given JWT token.
     *
     * @param token The JWT token to validate.
     * @throws RuntimeException if the token is invalid or expired.
     * @return true if the token is valid and not expired.
     */
    public static boolean validateToken(final String token) {
        DecodedJWT jwt = JWT.decode(token);
        Date expiresAt = jwt.getExpiresAt();

        if (expiresAt == null) {
            throw new JwtValidationException("Token does not contain an expiry date.", "401");
        }

        Instant now = Instant.now();
        if (expiresAt.toInstant().isBefore(now)) {
            throw new JwtValidationException("Invalid JWT token: expired", "401");
        }
        log.info("JWT is valid. Expires at: {}", expiresAt);
        return true;
    }

}
