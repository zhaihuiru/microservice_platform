package com.yn.anime.chat.dto;

import java.time.LocalDateTime;

public record ChatConversationDTO(
        Long id,
        String conversationType,
        String title,
        Long workId,
        Long ownerId,
        Integer status,
        Long memberCount,
        Long unreadCount,
        Long lastMessageId,
        String lastMessageContent,
        LocalDateTime lastMessageAt,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {
}