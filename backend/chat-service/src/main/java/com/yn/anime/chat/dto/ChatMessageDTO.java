package com.yn.anime.chat.dto;

import java.time.LocalDateTime;
import java.util.List;

public record ChatMessageDTO(
        Long id,
        Long conversationId,
        Long senderId,
        String clientMessageId,
        String messageType,
        String content,
        String mediaUrl,
        Long replyToMessageId,
        Boolean deleted,
        String reviewStatus,
        List<Long> mentionUserIds,
        LocalDateTime createdAt
) {
}