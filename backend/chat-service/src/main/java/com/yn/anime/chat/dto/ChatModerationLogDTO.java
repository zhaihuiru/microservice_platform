package com.yn.anime.chat.dto;

import java.time.LocalDateTime;

public record ChatModerationLogDTO(
        Long id,
        Long operatorId,
        Long conversationId,
        Long targetUserId,
        Long messageId,
        String operationType,
        String reason,
        LocalDateTime createdAt
) {
}