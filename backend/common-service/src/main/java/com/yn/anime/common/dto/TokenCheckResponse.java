package com.yn.anime.common.dto;

import java.util.List;

public record TokenCheckResponse(
        Long userId,
        String username,
        List<String> roles,
        Boolean valid
) {}