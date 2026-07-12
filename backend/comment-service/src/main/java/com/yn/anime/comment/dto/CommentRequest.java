package com.yn.anime.comment.dto;

/**
 * 评论请求 DTO
 */
public class CommentRequest {
    private Long workId;
    private String content;

    public Long getWorkId() { return workId; }
    public void setWorkId(Long workId) { this.workId = workId; }
    public String getContent() { return content; }
    public void setContent(String content) { this.content = content; }
}
