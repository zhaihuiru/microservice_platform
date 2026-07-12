package com.yn.anime.common.dto;

import java.time.LocalDateTime;
import java.util.List;

public record AdminUserDTO(
        Long id,
        String username,
        String email,
        String nickname,
        String avatarUrl,
        String bio,
        Integer status,
        List<String> roles,
        LocalDateTime lastLoginAt,
        LocalDateTime createdAt
) {}