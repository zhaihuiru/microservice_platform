package com.yn.anime.common.dto;

public record RegisterResponse(
        Long userId,
        String username,
        String nickname,
        String role
) {}