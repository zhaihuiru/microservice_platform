package com.yn.anime.favorite.dto;

/**
 * 收藏/取消收藏请求 DTO
 */
public class FavoriteRequest {
    private Long folderId;
    private Long workId;

    public Long getFolderId() { return folderId; }
    public void setFolderId(Long folderId) { this.folderId = folderId; }
    public Long getWorkId() { return workId; }
    public void setWorkId(Long workId) { this.workId = workId; }
}
