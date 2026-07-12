package com.yn.anime.chat.dto;

public record ChatWebSocketPushDTO(
        String type,
        Object data
) {
}