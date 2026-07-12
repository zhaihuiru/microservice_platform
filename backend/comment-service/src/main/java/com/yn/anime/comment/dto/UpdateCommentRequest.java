package com.yn.anime.comment.dto;

/**
 * 更新评论内容请求 DTO
 */
public class UpdateCommentRequest {
    private String content;

    public String getContent() { return content; }
    public void setContent(String content) { this.content = content; }
}
