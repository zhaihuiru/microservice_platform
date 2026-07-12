package com.yn.anime.rating.dto;

import java.math.BigDecimal;

/**
 * 作品平均分统计 DTO
 */
public class AvgRatingResponse {
    private Long workId;
    private BigDecimal avgScore;
    private Integer ratingCount;

    public Long getWorkId() { return workId; }
    public void setWorkId(Long workId) { this.workId = workId; }
    public BigDecimal getAvgScore() { return avgScore; }
    public void setAvgScore(BigDecimal avgScore) { this.avgScore = avgScore; }
    public Integer getRatingCount() { return ratingCount; }
    public void setRatingCount(Integer ratingCount) { this.ratingCount = ratingCount; }
}
