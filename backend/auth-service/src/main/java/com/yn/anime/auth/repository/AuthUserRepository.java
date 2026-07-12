package com.yn.anime.auth.repository;

import com.yn.anime.auth.entity.AuthUser;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface AuthUserRepository extends JpaRepository<AuthUser, Long> {
    Optional<AuthUser> findByUsername(String username);

    Optional<AuthUser> findByEmail(String email);

    boolean existsByUsername(String username);

    boolean existsByEmail(String email);

    @org.springframework.data.jpa.repository.Query("select u.id from AuthUser u where u.status = 0")
    java.util.List<Long> findActiveUserIds();
}