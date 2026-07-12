package com.yn.anime.chat.dto;

public record CreateWorkConversationRequest(
        Long workId,
        String title
) {
}