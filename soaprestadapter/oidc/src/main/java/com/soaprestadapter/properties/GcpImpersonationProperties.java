package com.soaprestadapter.properties;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;



/**
 * GCP Role based authentication
 */
@Data
@Configuration
@ConfigurationProperties(prefix = "gcp.impersonation")
public class GcpImpersonationProperties {
    /**
     * List of User Mappings
     */
    private List<UserMapping> users;

    /**
     * User Mapping details
     */
    @Data
    public static class UserMapping {
        /**
         * userName
         */
        private String username;
        /**
         * serviceAccount
         */
        private String serviceAccount;
        /**
         * List of actions for resources
         */
        private Map<String, List<String>> actions;
    }

    /**
     * GetUserMapping detail for eash user
     * @param username iterate for each user
     * @return UserMapping for particular userName
     */
    public Optional<UserMapping> getUser(final String username) {
        return users.stream()
                .filter(u -> u.getUsername().equalsIgnoreCase(username))
                .findFirst();
    }

}
