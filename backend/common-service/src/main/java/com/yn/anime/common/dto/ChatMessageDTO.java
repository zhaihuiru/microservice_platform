package com.yn.anime.common.dto;

public record ChatMessageDTO(Long id, Long conversationId, Long senderId, String messageType, String content) {}
