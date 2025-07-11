package com.soaprestadapter.service;

import com.soaprestadapter.properties.IamConfigProperties;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Collections;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class AWSLocalEntitlementServiceTest {

    @Mock
    private IamConfigProperties iamConfig;

    @InjectMocks
    private AwsIamLocalEntitlementService localEntitlementService;

    @Test
    void testIsUserEntitled_UserHasPermission_ReturnsTrue() {
        String username = "john.doe";
        String role = "admin";
        String action = "READ";

        Map<String, String> roleMappings = Map.of(username, role);
        Map<String, List<String>> permissions = Map.of(role, List.of("READ", "WRITE"));

        when(iamConfig.getRoleMappings()).thenReturn(roleMappings);
        when(iamConfig.getPermissions()).thenReturn(permissions);

        boolean result = localEntitlementService.isUserEntitled(username, action);

        assertTrue(result, "User should be entitled for READ action");
    }

    @Test
    void testIsUserEntitled_UserHasNoRole_ThrowsException() {
        String username = "unknown.user";
        when(iamConfig.getRoleMappings()).thenReturn(Collections.emptyMap());

        RuntimeException exception = assertThrows(RuntimeException.class, () ->
                localEntitlementService.isUserEntitled(username, "READ"));

        assertEquals("No role mapped for user: unknown.user", exception.getMessage());
    }

    @Test
    void testIsUserEntitled_RoleHasNoPermissions_ReturnsFalse() {
        String username = "jane.doe";
        String role = "viewer";

        Map<String, String> roleMappings = Map.of(username, role);
        Map<String, List<String>> permissions = Map.of(); // No permissions for role

        when(iamConfig.getRoleMappings()).thenReturn(roleMappings);
        when(iamConfig.getPermissions()).thenReturn(permissions);

        boolean result = localEntitlementService.isUserEntitled(username, "DELETE");

        assertFalse(result, "User should not be entitled since role has no permissions");
    }

    @Test
    void testIsUserEntitled_ActionNotInPermissionList_ReturnsFalse() {
        String username = "sam.user";
        String role = "editor";

        Map<String, String> roleMappings = Map.of(username, role);
        Map<String, List<String>> permissions = Map.of(role, List.of("WRITE", "UPDATE"));

        when(iamConfig.getRoleMappings()).thenReturn(roleMappings);
        when(iamConfig.getPermissions()).thenReturn(permissions);

        boolean result = localEntitlementService.isUserEntitled(username, "DELETE");

        assertFalse(result, "User should not be entitled for DELETE action");
    }
}
