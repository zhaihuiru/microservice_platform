package com.yn.anime.comment.dto;

import java.time.LocalDateTime;

/**
 * 评论响应 DTO
 */
public class CommentResponse {
    private Long id;
    private Long userId;
    private Long workId;
    private Long parentId;
    private String content;
    private Integer likeCount;
    private Boolean isSticky;
    private Boolean isEssence;
    private String status;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

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
