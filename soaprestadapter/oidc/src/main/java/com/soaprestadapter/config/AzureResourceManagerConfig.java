package com.soaprestadapter.config;

import com.azure.core.management.AzureEnvironment;
import com.azure.core.management.profile.AzureProfile;
import com.azure.identity.DefaultAzureCredentialBuilder;
import com.azure.resourcemanager.AzureResourceManager;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnExpression;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;



/**
 * Azure configuration
 */
@Configuration
@ConditionalOnExpression("'${entitlement.strategy.cloud-provider-type}' == 'AZURE'")
public class AzureResourceManagerConfig {
    /**
     * SubscriptionId
     */
    @Value("${azure.subscription-id}")
    private String subscriptionId;

    /**
     * Build AzureResourceManager for Azure
     * @return AzureResourceManager
     */
    @Bean("azureResourceManager")
    public AzureResourceManager buildAzureResourceManager()  {
        AzureProfile profile = new AzureProfile(AzureEnvironment.AZURE);
        return AzureResourceManager
                .authenticate(new DefaultAzureCredentialBuilder().build(), profile)
                .withSubscription(subscriptionId);
    }

}
