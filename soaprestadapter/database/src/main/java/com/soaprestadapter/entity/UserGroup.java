package com.soaprestadapter.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;

/**
 * This class represents the user group entity.
 */
@Entity
@Table(name = "tbl_user_group")
@Data
public class UserGroup {

    /**
     * This field represents the unique identifier for the user group.
     */
    @Id
    @Column(name = "user_group_id")
    private Long userGroupId;

    /**
     * This field represents the name of the role.
     */
    @Column(name = "role_name", nullable = false)
    private String roleName;

    /**
     *  This field is used to indicate if the user is authorized.
     */
    @Column(name = "is_authorized", nullable = false)
    private boolean isAuthorized;
}
