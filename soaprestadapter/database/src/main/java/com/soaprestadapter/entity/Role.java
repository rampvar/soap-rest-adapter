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
 *  Entity representing a role in the system.
 */
@Entity
@Table(name = "tbl_role")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Role {
    /**
     * Unique identifier for the role.
     */
    @Id
    @Column(name = "role_id")
    private Long roleId;

    /**
     * Name of the role.
     */
    @Column(name = "role_name", nullable = false)
    private String roleName;

    /**
     * Users associated with this role.
     */
    @ManyToMany
    @JoinTable(
            name = "tbl_role_group",
            joinColumns = @JoinColumn(name = "role_id"),
            inverseJoinColumns = @JoinColumn(name = "user_group_id")
    )
    private Set<UserGroup> userGroup;
}
