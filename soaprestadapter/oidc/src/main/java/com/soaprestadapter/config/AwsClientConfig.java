package com.soaprestadapter.config;

import org.springframework.boot.autoconfigure.condition.ConditionalOnExpression;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import software.amazon.awssdk.auth.credentials.DefaultCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.sts.StsClient;
import static com.soaprestadapter.constant.OidcConstant.AWS_REGION;

/**
 * AWS client configuration
 */
@Configuration
@ConditionalOnExpression("'${entitlement.strategy.cloud-provider-type}' == 'AWS'")
public class AwsClientConfig {

    /**
     * STS client bean configuration
     * @return StsClient
     */
    @Bean
    public StsClient stsClient() {
        return StsClient.builder()
                .region(Region.of(AWS_REGION))
                .credentialsProvider(DefaultCredentialsProvider.create())
                .build();
    }
}
