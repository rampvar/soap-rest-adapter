package com.soaprestadapter.factory;

import com.soaprestadapter.exception.IllegalArgumentException;
import com.soaprestadapter.service.AwsIamCloudEntitlementService;
import com.soaprestadapter.service.AwsIamLocalEntitlementService;
import com.soaprestadapter.service.EntitlementService;
import com.soaprestadapter.service.UserRoleGroupEntitlementService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;


/**
 * EntitlementFactory to load proper entitlement class
 */
@Component
@Slf4j
@RequiredArgsConstructor
public class EntitlementFactory {
    /**
     * HTTP 400 status code for bad request.
     */
    private static final int HTTP_BAD_REQUEST = 400;
    /**
     * Get Strategy from properties
     */
    @Value("${entitlement.strategy}")
    private String strategy;

    /**
     * Get AWS Environment from properties
     */
    @Value("${environment.aws}")
    private String awsEnvironment;

    /**
     * UserRoleGroupEntitlementService validate based on userID
     */
    private final UserRoleGroupEntitlementService userRoleGroupService;

    /**
     * AwsIamLocalEntitlementService validate in local aws
     */
    private final AwsIamLocalEntitlementService awsIamLocalEntitlementService;

    /**
     * AwsIamActualEntitlementService validate in actual aws
     */
    private final AwsIamCloudEntitlementService awsIamCloudEntitlementService;

    /**
     *  getEntitlementService based on application properties
     *  @return EntitlementService
     */

    public EntitlementService getEntitlementService() {
        return switch (strategy) {
            case "USER_ROLE_GROUP" -> userRoleGroupService;
            case "AWS_IAM" -> {
                if ("local".equalsIgnoreCase(awsEnvironment)) {
                    yield awsIamLocalEntitlementService;
                } else if ("actual".equalsIgnoreCase(awsEnvironment)) {
                    yield awsIamCloudEntitlementService;
                } else {
                    throw new IllegalArgumentException(HTTP_BAD_REQUEST,
                            "Invalid environment.aws value: " + awsEnvironment);
                }
            }
            default -> throw new IllegalArgumentException(HTTP_BAD_REQUEST,
                    "Invalid strategy: " + strategy);
        };
    }

}
