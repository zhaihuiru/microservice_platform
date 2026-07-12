package com.yn.anime.common.dto;

public record BroadcastNotificationRequest(
        String title,
        String content,
        String noticeType,
        String targetType,
        Long targetId
) {}