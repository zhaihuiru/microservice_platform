package com.yn.anime.common.dto;

public record NotificationCreateRequest(
        Long receiverId,
        Long senderId,
        String title,
        String content,
        String noticeType,
        String targetType,
        Long targetId
) {}