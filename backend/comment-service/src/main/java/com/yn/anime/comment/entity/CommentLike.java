package com.yn.anime.comment.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

/**
 * 评论点赞实体
 */
@Entity
@Table(name = "comment_like", uniqueConstraints = @UniqueConstraint(name = "uk_comment_user", columnNames = {"comment_id", "user_id"}))
public class CommentLike {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "comment_id", nullable = false)
    private Long commentId;

    @Column(name = "user_id", nullable = false)
    private Long userId;

    @Column(name = "created_at")
    private LocalDateTime createdAt = LocalDateTime.now();

    public CommentLike() {}

    public CommentLike(Long commentId, Long userId) {
        this.commentId = commentId;
        this.userId = userId;
        this.createdAt = LocalDateTime.now();
    }

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public Long getCommentId() { return commentId; }
    public void setCommentId(Long commentId) { this.commentId = commentId; }
    public Long getUserId() { return userId; }
    public void setUserId(Long userId) { this.userId = userId; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
}
