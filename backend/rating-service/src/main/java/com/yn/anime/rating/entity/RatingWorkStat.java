package com.yn.anime.rating.entity;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 作品评分统计实体
 */
@Entity
@Table(name = "rating_work_stat")
public class RatingWorkStat {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "work_id", nullable = false, unique = true)
    private Long workId;

    @Column(name = "avg_score", precision = 3, scale = 2)
    private BigDecimal avgScore = BigDecimal.ZERO;

    @Column(name = "rating_count")
    private Integer ratingCount = 0;

    @Column(name = "created_at")
    private LocalDateTime createdAt = LocalDateTime.now();

    @Column(name = "updated_at")
    private LocalDateTime updatedAt = LocalDateTime.now();

    public RatingWorkStat() {}

    public RatingWorkStat(Long workId) {
        this.workId = workId;
        this.avgScore = BigDecimal.ZERO;
        this.ratingCount = 0;
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public Long getWorkId() { return workId; }
    public void setWorkId(Long workId) { this.workId = workId; }
    public BigDecimal getAvgScore() { return avgScore; }
    public void setAvgScore(BigDecimal avgScore) { this.avgScore = avgScore; }
    public Integer getRatingCount() { return ratingCount; }
    public void setRatingCount(Integer ratingCount) { this.ratingCount = ratingCount; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }
}
