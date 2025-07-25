package com.soaprestadapter.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.auth.oauth2.AccessToken;
import com.google.auth.oauth2.GoogleCredentials;
import com.google.auth.oauth2.ImpersonatedCredentials;
import com.soaprestadapter.exception.IoException;
import com.soaprestadapter.factory.EntitlementService;
import com.soaprestadapter.properties.GcpImpersonationProperties;
import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
import static com.soaprestadapter.constant.OidcConstant.CLOUD_PLATFORM_SCOPE;
import static com.soaprestadapter.constant.OidcConstant.GCP_TOKEN_LIFETIME;
import static com.soaprestadapter.constant.OidcConstant.GOOGLE_TEST_URL;
import static java.net.HttpURLConnection.HTTP_INTERNAL_ERROR;
import static java.net.HttpURLConnection.HTTP_OK;


/**
 * Google Cloud Provider Entitlement Service
 */
@Service
@Slf4j
@Component("GCP")
@RequiredArgsConstructor
public class GcpEntitlementServiceImpl implements EntitlementService {
    /**
     * Properties for GCP impersonation
     */
    private final GcpImpersonationProperties impersonationProperties;
    /**
     * Object Mapper
     */
    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();

    /**
     *  UserEntitlement Method
     * @param username username of the user for whom the entitlement is checked
     * @param action   action to be performed
     * @return true or false user entitled or not
     */
    @Override
    public boolean isUserEntitled(final String username, final String action) {
        Optional<GcpImpersonationProperties.UserMapping> userDetails = impersonationProperties.getUser(username);
        log.info("User Entitlement Via GCP");
        if (userDetails.isEmpty()) {
            return false;
        }

        GcpImpersonationProperties.UserMapping user = userDetails.get();
        String targetSA = user.getServiceAccount();
        List<String> resources = user.getActions().getOrDefault(action, List.of());
        if (resources.isEmpty()) {
            return false;
        }

        try {
            //use this way approach while deploying inproduction
            //GoogleCredentials source = GoogleCredentials.getApplicationDefault();

            //use this for local testing ..we need service account.json file
            GoogleCredentials source = GoogleCredentials.fromStream(
                    Objects.requireNonNull(getClass().getClassLoader()
                            .getResourceAsStream("credentials/service-account.json"))
            );

            ImpersonatedCredentials impersonated = ImpersonatedCredentials.create(
                    source,
                    targetSA,
                    null,
                    List.of(CLOUD_PLATFORM_SCOPE),
                    GCP_TOKEN_LIFETIME
            );

            for (String resource : resources) {
                String permission = mapActionToPermission(action, resource);
                if (!checkPermission(impersonated, resource, permission)) {
                    log.info("Permission denied for {} on {}", permission, resource);
                    return false;
                }
            }

            return true;
        } catch (IOException e) {
            throw new IoException(HTTP_INTERNAL_ERROR,
                    "Failed impersonation or permission check", e.getMessage());
        }
    }

    /**
     * Validate permission via google provoded Rest Template call WE CAN DO VIA TestIamPermissionsRequest and
     *  TestIamPermissionsResponse whcih need protobuf depedency to be injected
     * @param credentials google credentials
     * @param resource resource mapping
     * @param permission permission requested
     * @return boolean is user valid or not
     */

    private boolean checkPermission(final GoogleCredentials credentials,
                                           final String resource, final String permission) {
        try {
            log.info("User Entitlement Via GCP : Checking Permission");

            credentials.refreshIfExpired();
            AccessToken token = credentials.getAccessToken();

            String projectId = extractProjectId(resource);
            String url = GOOGLE_TEST_URL + projectId + ":testIamPermissions";

            String requestBody = OBJECT_MAPPER.writeValueAsString(Map.of("permissions", List.of(permission)));

            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(url))
                    .header("Authorization", "Bearer " + token.getTokenValue())
                    .header("Content-Type", "application/json")
                    .POST(HttpRequest.BodyPublishers.ofString(requestBody))
                    .build();

            HttpResponse<String> response = HttpClient.newHttpClient()
                    .send(request, HttpResponse.BodyHandlers.ofString());

            if (response.statusCode() != HTTP_OK) {
                log.warn("GCP IAM REST returned status {} for {}: {}", response.statusCode(),
                        resource, response.body());
                return false;
            }

            JsonNode json = OBJECT_MAPPER.readTree(response.body());
            JsonNode permissionsNode = json.get("permissions");
            return permissionsNode != null && permissionsNode.toString().contains(permission);

        } catch (IOException | InterruptedException e) {
            throw new RuntimeException("Error calling GCP IAM REST API: {}", e);
        }
    }

    private String extractProjectId(final String resource) {
        if (resource.startsWith("projects/")) {
            return resource.substring("projects/".length());
        }
        throw new IllegalArgumentException("Unsupported resource format: " + resource);
    }

    private String mapActionToPermission(final String action, final String resource) {
        if (resource.contains("/buckets/")) {
            return switch (action) {
                case "read" -> "storage.objects.get";
                case "write" -> "storage.objects.create";
                default -> "storage.buckets.get";
            };
        } else if (resource.contains("/topics/")) {
            return switch (action) {
                case "read" -> "pubsub.topics.get";
                case "write" -> "pubsub.topics.publish";
                default -> "pubsub.topics.get";
            };
        } else {
            return switch (action) {
                case "read" -> "resourcemanager.projects.get";
                case "write" -> "resourcemanager.projects.update";
                default -> "resourcemanager.projects.get";
            };
        }
    }
}