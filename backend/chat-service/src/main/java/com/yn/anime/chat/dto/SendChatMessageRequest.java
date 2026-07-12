package com.yn.anime.chat.dto;

import java.util.List;

public record SendChatMessageRequest(
        String clientMessageId,
        String messageType,
        String content,
        String mediaUrl,
        Long replyToMessageId,
        List<Long> mentionUserIds
) {
}