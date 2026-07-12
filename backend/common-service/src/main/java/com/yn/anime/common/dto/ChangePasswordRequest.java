package com.yn.anime.common.dto;

public record ChangePasswordRequest(
        String oldPassword,
        String newPassword
) {}