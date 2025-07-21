package com.soaprestadapter.service;

import java.util.Map;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import software.amazon.awssdk.auth.credentials.AwsSessionCredentials;
import software.amazon.awssdk.auth.credentials.DefaultCredentialsProvider;
import software.amazon.awssdk.services.sts.StsClient;
import software.amazon.awssdk.services.sts.model.AssumeRoleRequest;
import software.amazon.awssdk.services.sts.model.AssumeRoleResponse;
import software.amazon.awssdk.services.sts.model.Credentials;
import software.amazon.awssdk.services.sts.model.StsException;

/**
 * AwsIamActualEntitlementService implementation.
 */
@Service
@Slf4j
public class AwsIamCloudEntitlementService implements EntitlementService {


    /**
     * Simulate a permission check by checking if the provided action is allowed for the given role ARN
     * @param username username of the user for whom the entitlement is checked
     * @param action action to be performed
     * @return
     */
    @Override
    public boolean isUserEntitled(final String username, final String action) {
        log.info("Actual AWS Iam Entitlement");
        String roleArn = getRoleArnForUser(username);
        if (roleArn == null) {
            return false;
        }
        try {
            StsClient stsClient1 = StsClient.builder()
                    .credentialsProvider(DefaultCredentialsProvider.create())
                    .build();

            AssumeRoleRequest assumeRoleRequest = AssumeRoleRequest.builder()
                    .roleArn(roleArn)
                    .roleSessionName("session-" + username)
                    .build();

            AssumeRoleResponse assumeRoleResponse = stsClient1.assumeRole(assumeRoleRequest);
            Credentials tempCredentials = assumeRoleResponse.credentials();

            AwsSessionCredentials.create(
                    tempCredentials.accessKeyId(),
                    tempCredentials.secretAccessKey(),
                    tempCredentials.sessionToken());
            return simulatePermissionCheck(roleArn, action);
        } catch (StsException e) {
            throw new RuntimeException("Failed to assume role", e);
        }

    }

    /**
     * Get the ARN of the IAM role for a given username
     *
     * @param username the username for which to get the role ARN
     * @return the ARN of the IAM role for the given username, or null if the username is not found in the map
     */
    private String getRoleArnForUser(final String username) {
        Map<String, String> userRoleMap = Map.of(
                "alice", "arn:aws:iam::123456789012:role/AdminRole",
                "bob", "arn:aws:iam::123456789012:role/ReadOnlyRole"
        );
        return userRoleMap.get(username);

    }

    /**
     * Simulate permission check using temporary credentials
     *
     * @param roleArn the ARN of the role to assume for permission check
     * @param action  the action to check for permission on the AWS resource (e.g., read, write, etc.)
     * @return true if the user has permission, false otherwise
     */

    private boolean simulatePermissionCheck(final String roleArn, final String action) {

        if (roleArn.contains("AdminRole")) {
            return true;
        } else {
            return roleArn.contains("ReadOnlyRole") && "read".equals(action);
        }
    }

}
