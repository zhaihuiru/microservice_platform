package com.yn.anime.common.dto;

public record RegisterRequest(
        String username,
        String email,
        String password,
        String nickname
) {}