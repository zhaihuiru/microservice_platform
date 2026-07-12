package com.yn.anime.favorite.dto;

/**
 * 创建收藏夹请求 DTO
 */
public class FolderRequest {
    private String folderName;
    private Boolean isPublic;

    public String getFolderName() { return folderName; }
    public void setFolderName(String folderName) { this.folderName = folderName; }
    public Boolean getIsPublic() { return isPublic; }
    public void setIsPublic(Boolean isPublic) { this.isPublic = isPublic; }
}
