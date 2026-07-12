package com.yn.anime.auth.repository;

import com.yn.anime.auth.entity.AuthUserRole;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface AuthUserRoleRepository extends JpaRepository<AuthUserRole, Long> {
    List<AuthUserRole> findByUserId(Long userId);

    boolean existsByUserIdAndRoleId(Long userId, Long roleId);

    void deleteByUserId(Long userId);
}