package com.soaprestadapter.service;

import com.soaprestadapter.factory.EntitlementService;
import com.soaprestadapter.properties.IamConfigProperties;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

/**
 *      This is an implementation of the EntitlementService interface for AWS IAM local.
 */
@Service
@Slf4j
@RequiredArgsConstructor
public class AwsIamLocalEntitlementService implements EntitlementService {
    /**
     *      Role mappings for AWS IAM local.
     */
    private final IamConfigProperties iamConfig;

    /**
     * Checks if a user is entitled to perform a specific action.
     * @param username The username for which the entitlement is to be checked.
     * @param action    The action for which the entitlement is to be checked.
     * @return true if the user is entitled, false otherwise.
     */
    @Override
    public boolean isUserEntitled(final String username, final String action) {

        log.info("Local AWS Entitlement");
        log.info("IAM Role mappings: {}", iamConfig.getRoleMappings());
        log.info("Received username: {}", username);
        String role = iamConfig.getRoleMappings().get(username);
        if (role == null) {
            throw new RuntimeException("No role mapped for user: " + username);
        }
        List<String> allowedActions = iamConfig.getPermissions().get(role);
        return allowedActions != null && allowedActions.contains(action);
    }
}
