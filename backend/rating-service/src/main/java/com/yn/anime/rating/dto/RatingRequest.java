package com.yn.anime.rating.dto;

/**
 * 评分请求 DTO
 */
public class RatingRequest {
    private Long workId;
    private Integer score;

    public Long getWorkId() { return workId; }
    public void setWorkId(Long workId) { this.workId = workId; }
    public Integer getScore() { return score; }
    public void setScore(Integer score) { this.score = score; }
}
