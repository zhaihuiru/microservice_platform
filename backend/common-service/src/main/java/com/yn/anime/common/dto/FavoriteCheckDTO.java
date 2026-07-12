package com.yn.anime.common.dto;

public record FavoriteCheckDTO(Long userId, String targetType, Long targetId, Boolean collected) {}
