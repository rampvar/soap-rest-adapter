package com.soaprestadapter.factory;

import com.soaprestadapter.service.AwsIamActualEntitlementService;
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
     *
     */
    @Value("${entitlement.strategy}")
    private String strategy;

    /**
     *
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
    private final AwsIamLocalEntitlementService awsIamLocalService;

    /**
     * AwsIamActualEntitlementService validate in actual aws
     */
    private final AwsIamActualEntitlementService awsIamActualService;

    /**
     *  getEntitlementService based on application properties
     *  @return EntitlementService
     */
    public EntitlementService getEntitlementService() {
        return switch (strategy) {
            case "USER_ROLE_GROUP" -> userRoleGroupService;
            case "AWS_IAM" -> {
                if ("local".equalsIgnoreCase(awsEnvironment)) {
                    yield awsIamLocalService;
                } else if ("actual".equalsIgnoreCase(awsEnvironment)) {
                    yield awsIamActualService;
                } else {
                    throw new IllegalArgumentException("Invalid environment.aws value: " + awsEnvironment);
                }
            }
            default -> throw new IllegalArgumentException("Invalid strategy: " + strategy);
        };
    }

}
