package com.yn.anime.rating.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

/**
 * 投票记录实体
 */
@Entity
@Table(name = "rating_vote_record", uniqueConstraints = @UniqueConstraint(name = "uk_user_topic", columnNames = {"user_id", "topic_id"}))
public class RatingVoteRecord {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "user_id", nullable = false)
    private Long userId;

    @Column(name = "topic_id", nullable = false)
    private Long topicId;

    @Column(name = "target_id", nullable = false)
    private Long targetId;

    @Column(name = "created_at")
    private LocalDateTime createdAt = LocalDateTime.now();

    public RatingVoteRecord() {}

    public RatingVoteRecord(Long userId, Long topicId, Long targetId) {
        this.userId = userId;
        this.topicId = topicId;
        this.targetId = targetId;
        this.createdAt = LocalDateTime.now();
    }

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public Long getUserId() { return userId; }
    public void setUserId(Long userId) { this.userId = userId; }
    public Long getTopicId() { return topicId; }
    public void setTopicId(Long topicId) { this.topicId = topicId; }
    public Long getTargetId() { return targetId; }
    public void setTargetId(Long targetId) { this.targetId = targetId; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
}
