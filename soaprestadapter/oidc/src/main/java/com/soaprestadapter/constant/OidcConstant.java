package com.soaprestadapter.constant;

/**
 * Constant class for OIDC module
 */
public class OidcConstant {

    /**
     * CLOUD PROVIDER - AWS
     */
    public static final String AWS = "AWS";
    /**
     * CLOUD PROVIDER - AZURE
     */
    public static final String AZURE = "AZURE";
    /**
     * CLOUD PROVIDER - GCP
     */
    public static final String GCP = "GCP";
    /**
     * ENTITLEMENT STRATEGY - USER_ROLE_GROUP
     */
    public static final String DATABASE = "DB";
    /**
     * ENTITLEMENT STRATEGY - CLOUD
     */
    public static final String CLOUD_PROVIDER = "CLOUD";
    /**
     * AZURE_SCOPE
     */
    public static final String AZURE_SCOPE = "/subscriptions/";

    /**
     * GCP Impersonated Token lifetime
     */
    public static final int  GCP_TOKEN_LIFETIME = 3600;
    /**
     * CLOUD_PLATFORM_SCOPE
     */
    public static final String CLOUD_PLATFORM_SCOPE = "https://www.googleapis.com/auth/cloud-platform";

    /**
     * GOOGLE_TEST_URL url to test iam permissions
     */
    public static final String GOOGLE_TEST_URL = "https://cloudresourcemanager.googleapis.com/v1/projects/";

    /**
     * AWS_REGION
     */
    public static final String AWS_REGION = "us-east-1";
}
