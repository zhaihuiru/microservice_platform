package com.yn.anime.favorite.controller;

import com.yn.anime.common.dto.WorkDTO;
import com.yn.anime.common.response.ApiResponse;
import com.yn.anime.favorite.client.WorkClient;
import com.yn.anime.favorite.dto.*;
import com.yn.anime.favorite.service.FavoriteService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 收藏服务控制器
 * 路径前缀：/api/favorites
 */
@RestController
@RequestMapping("/api/favorites")
public class FavoriteController {

    private final FavoriteService favoriteService;
    private final WorkClient workClient;

    public FavoriteController(FavoriteService favoriteService, WorkClient workClient) {
        this.favoriteService = favoriteService;
        this.workClient = workClient;
    }

    @GetMapping("/ping")
    public String ping() {
        return "favorite-service is running";
    }

    /**
     * 创建收藏夹
     * 从请求头 User-Id 获取当前用户ID
     */
    @PostMapping("/folders")
    public ApiResponse<FolderResponse> createFolder(
            @RequestBody FolderRequest request,
            @RequestHeader("User-Id") Long userId) {
        try {
            FolderResponse response = favoriteService.createFolder(request, userId);
            return ApiResponse.success(response);
        } catch (Exception e) {
            return ApiResponse.fail(500, e.getMessage());
        }
    }

    /**
     * 获取我的收藏夹列表
     * 从请求头 User-Id 获取当前用户ID
     */
    @GetMapping("/folders/me")
    public ApiResponse<List<FolderResponse>> getMyFolders(
            @RequestHeader("User-Id") Long userId) {
        List<FolderResponse> folders = favoriteService.getMyFolders(userId);
        return ApiResponse.success(folders);
    }

    /**
     * 获取他人的公开收藏夹
     * 只返回 is_public=1 的收藏夹，所有用户可访问
     */
    @GetMapping("/folders/user/{userId}")
    public ApiResponse<List<FolderResponse>> getUserPublicFolders(
            @PathVariable Long userId) {
        List<FolderResponse> folders = favoriteService.getUserPublicFolders(userId);
        return ApiResponse.success(folders);
    }

    /**
     * 删除收藏夹
     * 从请求头 User-Id 获取当前用户ID，校验是否为本人
     */
    @DeleteMapping("/folders/{folderId}")
    public ApiResponse<Void> deleteFolder(
            @PathVariable Long folderId,
            @RequestHeader("User-Id") Long userId) {
        try {
            favoriteService.deleteFolder(folderId, userId);
            return ApiResponse.success(null);
        } catch (RuntimeException e) {
            return ApiResponse.fail(403, e.getMessage());
        }
    }

    /**
     * 收藏作品到指定收藏夹
     * 从请求头 User-Id 获取当前用户ID，先调用 work-service 验证作品是否存在
     */
    @PostMapping
    public ApiResponse<FavoriteItemResponse> addFavorite(
            @RequestBody FavoriteRequest request,
            @RequestHeader("User-Id") Long userId) {
        try {
            // 调用 work-service 验证作品是否存在
            WorkDTO work = workClient.getWorkById(request.getWorkId());
            if (work == null || work.getId() == null) {
                return ApiResponse.fail(404, "作品不存在");
            }
            FavoriteItemResponse response = favoriteService.addFavorite(request, userId);
            return ApiResponse.success(response);
        } catch (RuntimeException e) {
            return ApiResponse.fail(400, e.getMessage());
        }
    }

    /**
     * 取消收藏
     * 从请求头 User-Id 获取当前用户ID，校验是否为本人
     */
    @DeleteMapping
    public ApiResponse<Void> removeFavorite(
            @RequestBody FavoriteRequest request,
            @RequestHeader("User-Id") Long userId) {
        try {
            favoriteService.removeFavorite(request, userId);
            return ApiResponse.success(null);
        } catch (RuntimeException e) {
            return ApiResponse.fail(404, e.getMessage());
        }
    }

    /**
     * 获取收藏夹内容
     * 所有用户可访问（Service层会检查收藏夹是否存在）
     */
    @GetMapping("/folder/{folderId}/items")
    public ApiResponse<List<FavoriteItemResponse>> getFolderItems(
            @PathVariable Long folderId) {
        try {
            List<FavoriteItemResponse> items = favoriteService.getFolderItems(folderId);
            return ApiResponse.success(items);
        } catch (RuntimeException e) {
            return ApiResponse.fail(404, e.getMessage());
        }
    }

    /**
     * 管理员：删除任意收藏夹
     * 从请求头 User-Role 校验管理员权限
     */
    @DeleteMapping("/admin/folders/{folderId}")
    public ApiResponse<Void> adminDeleteFolder(
            @PathVariable Long folderId,
            @RequestHeader("User-Role") String userRole) {
        if (!"ADMIN".equals(userRole)) {
            return ApiResponse.fail(403, "权限不足，仅管理员可操作");
        }
        try {
            favoriteService.adminDeleteFolder(folderId);
            return ApiResponse.success(null);
        } catch (RuntimeException e) {
            return ApiResponse.fail(404, e.getMessage());
        }
    }

    // ==================== 供 A（work-service）Feign 调用的兼容接口 ====================

    /**
     * 检查是否已收藏（供 work-service 内部 Feign 调用，兼容旧接口）
     */
    @GetMapping("/check")
    public com.yn.anime.common.dto.FavoriteCheckDTO check(
            @RequestParam Long userId,
            @RequestParam String targetType,
            @RequestParam Long targetId) {
        if (!"WORK".equals(targetType)) {
            return new com.yn.anime.common.dto.FavoriteCheckDTO(userId, targetType, targetId, false);
        }
        // 检查用户是否在任意收藏夹中收藏了该作品（简化实现：返回未收藏）
        return new com.yn.anime.common.dto.FavoriteCheckDTO(userId, targetType, targetId, false);
    }
}
