package com.yn.anime.comment.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

/**
 * 评论实体
 */
@Entity
@Table(name = "comment_comment")
public class Comment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "user_id", nullable = false)
    private Long userId;

    @Column(name = "work_id", nullable = false)
    private Long workId;

    @Column(name = "parent_id")
    private Long parentId;

    @Column(columnDefinition = "TEXT", nullable = false)
    private String content;

    @Column(name = "like_count")
    private Integer likeCount = 0;

    @Column(name = "is_sticky")
    private Boolean isSticky = false;

    @Column(name = "is_essence")
    private Boolean isEssence = false;

    @Column(name = "status")
    private String status = "PENDING";

    @Column(name = "created_at")
    private LocalDateTime createdAt = LocalDateTime.now();

    @Column(name = "updated_at")
    private LocalDateTime updatedAt = LocalDateTime.now();

    public Comment() {}

    public Comment(Long userId, Long workId, Long parentId, String content) {
        this.userId = userId;
        this.workId = workId;
        this.parentId = parentId;
        this.content = content;
        this.likeCount = 0;
        this.isSticky = false;
        this.isEssence = false;
        this.status = "PENDING";
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public Long getUserId() { return userId; }
    public void setUserId(Long userId) { this.userId = userId; }
    public Long getWorkId() { return workId; }
    public void setWorkId(Long workId) { this.workId = workId; }
    public Long getParentId() { return parentId; }
    public void setParentId(Long parentId) { this.parentId = parentId; }
    public String getContent() { return content; }
    public void setContent(String content) { this.content = content; }
    public Integer getLikeCount() { return likeCount; }
    public void setLikeCount(Integer likeCount) { this.likeCount = likeCount; }
    public Boolean getIsSticky() { return isSticky; }
    public void setIsSticky(Boolean isSticky) { this.isSticky = isSticky; }
    public Boolean getIsEssence() { return isEssence; }
    public void setIsEssence(Boolean isEssence) { this.isEssence = isEssence; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }
}
