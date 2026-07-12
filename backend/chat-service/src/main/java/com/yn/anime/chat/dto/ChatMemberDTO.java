package com.yn.anime.chat.dto;

import java.time.LocalDateTime;

public record ChatMemberDTO(
        Long id,
        Long conversationId,
        Long userId,
        String memberRole,
        Boolean muted,
        Boolean deleted,
        Long lastReadMessageId,
        LocalDateTime joinedAt,
        LocalDateTime updatedAt
) {
}