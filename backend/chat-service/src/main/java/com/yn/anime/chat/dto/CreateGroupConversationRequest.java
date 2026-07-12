package com.yn.anime.chat.dto;

import java.util.List;

public record CreateGroupConversationRequest(
        String title,
        List<Long> memberIds
) {
}