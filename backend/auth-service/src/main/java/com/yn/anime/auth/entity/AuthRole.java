package com.yn.anime.auth.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "auth_role")
public class AuthRole {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "role_code", nullable = false, unique = true, length = 50)
    private String roleCode;

    @Column(name = "role_name", nullable = false, length = 50)
    private String roleName;

    @Column(length = 255)
    private String description;

    public Long getId() {
        return id;
    }

    public String getRoleCode() {
        return roleCode;
    }

    public String getRoleName() {
        return roleName;
    }

    public String getDescription() {
        return description;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public void setRoleCode(String roleCode) {
        this.roleCode = roleCode;
    }

    public void setRoleName(String roleName) {
        this.roleName = roleName;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}