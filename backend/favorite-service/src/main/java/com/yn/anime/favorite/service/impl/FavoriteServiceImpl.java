package com.yn.anime.favorite.service.impl;

import com.yn.anime.favorite.dto.*;
import com.yn.anime.favorite.entity.FavFolder;
import com.yn.anime.favorite.entity.FavoriteItem;
import com.yn.anime.favorite.repository.FavFolderRepository;
import com.yn.anime.favorite.repository.FavoriteItemRepository;
import com.yn.anime.favorite.service.FavoriteService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * 收藏服务实现
 */
@Service
public class FavoriteServiceImpl implements FavoriteService {

    private final FavFolderRepository favFolderRepository;
    private final FavoriteItemRepository favoriteItemRepository;

    public FavoriteServiceImpl(FavFolderRepository favFolderRepository,
                               FavoriteItemRepository favoriteItemRepository) {
        this.favFolderRepository = favFolderRepository;
        this.favoriteItemRepository = favoriteItemRepository;
    }

    @Override
    @Transactional
    public FolderResponse createFolder(FolderRequest request, Long userId) {
        FavFolder folder = new FavFolder(userId, request.getFolderName(),
                request.getIsPublic() != null ? request.getIsPublic() : false);
        favFolderRepository.save(folder);
        return toFolderResponse(folder);
    }

    @Override
    public List<FolderResponse> getMyFolders(Long userId) {
        List<FavFolder> folders = favFolderRepository.findByUserIdOrderByCreatedAtDesc(userId);
        return folders.stream()
                .map(this::toFolderResponse)
                .collect(Collectors.toList());
    }

    @Override
    public List<FolderResponse> getUserPublicFolders(Long userId) {
        // 只返回 is_public=1 的收藏夹
        List<FavFolder> folders = favFolderRepository.findByUserIdAndIsPublicTrueOrderByCreatedAtDesc(userId);
        return folders.stream()
                .map(this::toFolderResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public void deleteFolder(Long folderId, Long userId) {
        FavFolder folder = favFolderRepository.findById(folderId)
                .orElseThrow(() -> new RuntimeException("收藏夹不存在"));

        // 校验是否为本人操作
        if (!folder.getUserId().equals(userId)) {
            throw new RuntimeException("无权删除他人的收藏夹");
        }

        // 先删除收藏夹下的所有收藏项
        favoriteItemRepository.deleteByFolderId(folderId);
        // 再删除收藏夹
        favFolderRepository.delete(folder);
    }

    @Override
    @Transactional
    public FavoriteItemResponse addFavorite(FavoriteRequest request, Long userId) {
        // 验证收藏夹是否存在且属于当前用户
        FavFolder folder = favFolderRepository.findById(request.getFolderId())
                .orElseThrow(() -> new RuntimeException("收藏夹不存在"));

        if (!folder.getUserId().equals(userId)) {
            throw new RuntimeException("无权向他人收藏夹添加内容");
        }

        // 检查是否已收藏
        Optional<FavoriteItem> existing = favoriteItemRepository.findByFolderIdAndWorkId(
                request.getFolderId(), request.getWorkId());
        if (existing.isPresent()) {
            throw new RuntimeException("该作品已在收藏夹中");
        }

        FavoriteItem item = new FavoriteItem(request.getFolderId(), request.getWorkId());
        favoriteItemRepository.save(item);

        // 更新收藏夹更新时间
        folder.setUpdatedAt(LocalDateTime.now());
        favFolderRepository.save(folder);

        return toFavoriteItemResponse(item);
    }

    @Override
    @Transactional
    public void removeFavorite(FavoriteRequest request, Long userId) {
        // 验证收藏夹是否存在且属于当前用户
        FavFolder folder = favFolderRepository.findById(request.getFolderId())
                .orElseThrow(() -> new RuntimeException("收藏夹不存在"));

        if (!folder.getUserId().equals(userId)) {
            throw new RuntimeException("无权操作他人的收藏夹");
        }

        Optional<FavoriteItem> item = favoriteItemRepository.findByFolderIdAndWorkId(
                request.getFolderId(), request.getWorkId());
        if (item.isEmpty()) {
            throw new RuntimeException("未找到该收藏记录");
        }

        favoriteItemRepository.delete(item.get());

        // 更新收藏夹更新时间
        folder.setUpdatedAt(LocalDateTime.now());
        favFolderRepository.save(folder);
    }

    @Override
    public List<FavoriteItemResponse> getFolderItems(Long folderId) {
        // 查看收藏夹内容（只对公开收藏夹或在用户权限检查由Controller处理）
        FavFolder folder = favFolderRepository.findById(folderId)
                .orElseThrow(() -> new RuntimeException("收藏夹不存在"));

        List<FavoriteItem> items = favoriteItemRepository.findByFolderIdOrderByCreatedAtDesc(folderId);
        return items.stream()
                .map(this::toFavoriteItemResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public void adminDeleteFolder(Long folderId) {
        FavFolder folder = favFolderRepository.findById(folderId)
                .orElseThrow(() -> new RuntimeException("收藏夹不存在"));

        // 先删除收藏夹下的所有收藏项
        favoriteItemRepository.deleteByFolderId(folderId);
        // 再删除收藏夹
        favFolderRepository.delete(folder);
    }

    /**
     * 收藏夹实体转响应DTO
     */
    private FolderResponse toFolderResponse(FavFolder folder) {
        FolderResponse response = new FolderResponse();
        response.setId(folder.getId());
        response.setUserId(folder.getUserId());
        response.setFolderName(folder.getFolderName());
        response.setIsPublic(folder.getIsPublic());
        response.setCreatedAt(folder.getCreatedAt());
        response.setUpdatedAt(folder.getUpdatedAt());
        return response;
    }

    /**
     * 收藏项实体转响应DTO
     */
    private FavoriteItemResponse toFavoriteItemResponse(FavoriteItem item) {
        FavoriteItemResponse response = new FavoriteItemResponse();
        response.setId(item.getId());
        response.setFolderId(item.getFolderId());
        response.setWorkId(item.getWorkId());
        response.setCreatedAt(item.getCreatedAt());
        return response;
    }
}
