package com.yn.anime.rating.dto;

/**
 * 投票请求 DTO
 */
public class VoteRequest {
    private Long topicId;
    private Long targetId;

    public Long getTopicId() { return topicId; }
    public void setTopicId(Long topicId) { this.topicId = topicId; }
    public Long getTargetId() { return targetId; }
    public void setTargetId(Long targetId) { this.targetId = targetId; }
}
