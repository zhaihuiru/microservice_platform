package com.yn.anime.common.dto;

public record BroadcastResultDTO(
        Long messageId,
        String title,
        Integer receiverCount
) {}