package com.yn.anime.common.dto;

public record CommentDTO(Long id, Long userId, String targetType, Long targetId, String content, Long likeCount) {}
