package com.yn.anime.comment.dto;

/**
 * 审核评论请求 DTO
 */
public class UpdateCommentStatusRequest {
    private String status;

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
}
