package com.soaprestadapter.service;

import com.azure.core.http.rest.PagedIterable;
import com.azure.resourcemanager.AzureResourceManager;
import com.azure.resourcemanager.authorization.models.RoleAssignment;
import com.azure.resourcemanager.authorization.models.RoleDefinition;
import com.soaprestadapter.factory.EntitlementService;
import com.soaprestadapter.properties.AzureEntitlementProperties;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnExpression;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
import static com.soaprestadapter.constant.OidcConstant.AZURE_SCOPE;


/**
 * Azure Entitlement Service
 */
@Service
@Slf4j
@Component("AZURE")
@ConditionalOnExpression("'${entitlement.strategy.cloud-provider-type}' == 'AZURE'")
@RequiredArgsConstructor
public class AzureEntitlementServiceImpl implements EntitlementService {
    /**
     * Azure Subscription Id
     */
    @Value("${azure.subscription-id}")
    private String subscriptionId;


    /**
     * Azure ResourceManager
     */
    private final AzureResourceManager azureResourceManager;

    /**
     * Azure role entitlement properties
     */
    private AzureEntitlementProperties azureEntitlementProperties;

    /**
     * UserEntitlement Method Azure Cloud
     *
     * @param username username of the user for whom the entitlement is checked
     * @param action   action to be performed
     * @return true or false user entitled or not
     */
    @Override
    public boolean isUserEntitled(final String username, final String action) {
        log.info("Inside Azure Cloud User validation");

        PagedIterable<RoleAssignment> assignments = azureResourceManager.accessManagement().
                roleAssignments().listByScope(AZURE_SCOPE + subscriptionId);

        for (RoleAssignment assignment : assignments) {
            if (assignment.principalId() != null && assignment.principalId().equalsIgnoreCase(username)) {
                RoleDefinition role = azureResourceManager.accessManagement()
                        .roleDefinitions()
                        .getById(assignment.roleDefinitionId());

                if (isRolePermitted(role, action)) {
                    return true;
                }
            }
        }
        return false;

    }

    private boolean isRolePermitted(final RoleDefinition role, final String action) {

        List<String> requiredAction = azureEntitlementProperties.getActionsFor(action);
        if (requiredAction == null) {
            return false;
        }

        List<String> allowedActions = role.permissions().stream()
                .flatMap(p -> p.actions().stream())
                .toList();

        return allowedActions.contains("*") || requiredAction.stream().anyMatch(allowedActions::contains);
    }

}
