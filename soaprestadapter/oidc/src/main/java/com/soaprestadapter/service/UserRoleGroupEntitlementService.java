package com.soaprestadapter.service;

import com.soaprestadapter.Repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

/**
 * Service for managing user role group entitlements.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class UserRoleGroupEntitlementService implements EntitlementService {

    /**
     * The user repository.
     */
    private final UserRepository userRepository;

    /**
     * Checks if a user is entitled to perform a specific action.
     *
     * @param username The username of the user.
     * @param action   The action to be performed.
     * @return True if the user is entitled, false otherwise.
     */
    @Override
    public boolean isUserEntitled(final String username, final String action) {
        return userRepository.isUserEntitled(Long.valueOf(username));
    }
}
