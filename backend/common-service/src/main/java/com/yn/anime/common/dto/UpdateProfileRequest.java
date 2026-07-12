package com.yn.anime.common.dto;

public record UpdateProfileRequest(
        String email,
        String nickname,
        String avatarUrl,
        String bio
) {}