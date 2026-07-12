package com.yn.anime.common.dto;

import java.time.LocalDateTime;

public record NotificationDTO(
        Long userMessageId,
        Long messageId,
        Long receiverId,
        Long senderId,
        String title,
        String content,
        String noticeType,
        String targetType,
        Long targetId,
        Integer readStatus,
        LocalDateTime readAt,
        LocalDateTime createdAt
) {}