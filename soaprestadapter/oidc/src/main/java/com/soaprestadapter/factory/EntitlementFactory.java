package com.soaprestadapter.factory;

import com.soaprestadapter.exception.IllegalArgumentException;
import com.soaprestadapter.properties.CloudProviderProperties;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;
import static com.soaprestadapter.constant.OidcConstant.AWS;
import static com.soaprestadapter.constant.OidcConstant.AZURE;
import static com.soaprestadapter.constant.OidcConstant.CLOUD_PROVIDER;
import static com.soaprestadapter.constant.OidcConstant.DATABASE;
import static com.soaprestadapter.constant.OidcConstant.GCP;
import static java.net.HttpURLConnection.HTTP_BAD_REQUEST;


/**
 * EntitlementFactory to load proper entitlement class
 */
@Component
@Slf4j
@RequiredArgsConstructor
public class EntitlementFactory {

    /**
     *  ApplicationContext for bean initialization
     */
    private final ApplicationContext context;
    /**
     * Cloud Provider Properties
     */
    private final CloudProviderProperties props;


    /**
     *  getEntitlementService based on application properties
     *  @return EntitlementService
     */

    public EntitlementService getEntitlementService() {

        String mode = props.getStrategy().getMode().toUpperCase();

        return switch (mode) {
            case DATABASE -> context.getBean(DATABASE, EntitlementService.class);
            case CLOUD_PROVIDER -> {
                String provider = props.getStrategy().getCloudProviderType().toUpperCase();
                yield switch (provider) {
                        case AWS, AZURE, GCP -> context.getBean(provider, EntitlementService.class);
                        default -> throw new IllegalArgumentException(HTTP_BAD_REQUEST,
                            "Unsupported cloud provider: " + provider);
                    };
            }
            default -> throw new IllegalArgumentException(HTTP_BAD_REQUEST,
                    "Invalid entitlement strategy mode: " + mode);
        };
    }
}
