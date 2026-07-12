package com.yn.anime.common.dto;

import java.util.List;

public record LoginResponse(
        Long userId,
        String username,
        String nickname,
        String avatarUrl,
        String token,
        List<String> roles
) {}