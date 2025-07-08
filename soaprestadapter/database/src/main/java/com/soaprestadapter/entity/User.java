package com.soaprestadapter.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.Table;
import java.util.Set;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * User entity representing a user in the system.
 */
@Entity
@Table(name = "tbl_user")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class User {
    /**
     * The unique identifier of the user.
     */
    @Id
    @Column(name = "user_id")
    private Long userId;

    /**
     *  The name of the user.
     */
    @Column(name = "user_name", nullable = false)
    private String userName;

    /**
     * Role associated with the user.
     */
    @ManyToMany
    @JoinTable(
            name = "tbl_user_role",
            joinColumns = @JoinColumn(name = "user_id"),
            inverseJoinColumns = @JoinColumn(name = "role_id")
    )
    private Set<Role> roles;
}
