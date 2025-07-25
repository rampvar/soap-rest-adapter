package com.soaprestadapter.factory;

/**
 * Service interface for entitlement management
 */
public interface EntitlementService {
        /**
         * Check if a user is entitled to perform a specific action
         * @param username username of the user for whom the entitlement is checked
         * @param action action to be performed
         * @return true if entitled, false otherwise
         */
    boolean isUserEntitled(String username, String action);
}
