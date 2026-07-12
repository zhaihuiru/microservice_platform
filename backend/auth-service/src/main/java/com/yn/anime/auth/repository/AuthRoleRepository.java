package com.yn.anime.auth.repository;

import com.yn.anime.auth.entity.AuthRole;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface AuthRoleRepository extends JpaRepository<AuthRole, Long> {
    Optional<AuthRole> findByRoleCode(String roleCode);
}