package com.soaprestadapter.Repository;

import com.soaprestadapter.entity.User;
import org.springframework.context.annotation.Profile;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

/**
 * This repository is used for interacting with the User entity.
 */
@Repository
@Profile("!sqlite")
public interface UserRepository extends JpaRepository<User, Long> {

    /**
     * Checks if a user is entitled to a specific user group.
     *
     * @param userId The user ID.
     * @return is_authorized
     */
    @Query("""
                SELECT CASE WHEN COUNT(g) > 0 THEN TRUE ELSE FALSE END
                FROM User u
                JOIN u.roles r
                JOIN r.userGroup g
                WHERE u.id = :userId AND g.isAuthorized = true
            """)
    boolean isUserEntitled(@Param("userId") Long userId);

}
