package com.yn.anime.favorite.service;

import com.yn.anime.favorite.dto.*;

import java.util.List;

/**
 * 收藏服务接口
 */
public interface FavoriteService {

    /**
     * 创建收藏夹
     */
    FolderResponse createFolder(FolderRequest request, Long userId);

    /**
     * 获取我的收藏夹列表
     */
    List<FolderResponse> getMyFolders(Long userId);

    /**
     * 获取他人的公开收藏夹
     */
    List<FolderResponse> getUserPublicFolders(Long userId);

    /**
     * 删除自己的收藏夹
     */
    void deleteFolder(Long folderId, Long userId);

    /**
     * 收藏作品到指定收藏夹
     */
    FavoriteItemResponse addFavorite(FavoriteRequest request, Long userId);

    /**
     * 取消收藏
     */
    void removeFavorite(FavoriteRequest request, Long userId);

    /**
     * 获取收藏夹内容
     */
    List<FavoriteItemResponse> getFolderItems(Long folderId);

    /**
     * 管理员：删除任意收藏夹
     */
    void adminDeleteFolder(Long folderId);
}
