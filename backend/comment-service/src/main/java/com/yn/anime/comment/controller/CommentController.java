package com.yn.anime.comment.controller;

import com.yn.anime.comment.client.WorkClient;
import com.yn.anime.comment.dto.*;
import com.yn.anime.comment.service.CommentService;
import com.yn.anime.common.dto.WorkDTO;
import com.yn.anime.common.response.ApiResponse;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 评论服务控制器
 * 路径前缀：/api/comments
 */
@RestController
@RequestMapping("/api/comments")
public class CommentController {

    private final CommentService commentService;
    private final WorkClient workClient;

    public CommentController(CommentService commentService, WorkClient workClient) {
        this.commentService = commentService;
        this.workClient = workClient;
    }

    @GetMapping("/ping")
    public String ping() {
        return "comment-service is running";
    }

    /**
     * 发表评论
     * 从请求头 User-Id 获取当前用户ID，先调用 work-service 验证作品是否存在
     */
    @PostMapping
    public ApiResponse<CommentResponse> addComment(
            @RequestBody CommentRequest request,
            @RequestHeader("User-Id") Long userId) {
        try {
            // 调用 work-service 验证作品是否存在
            WorkDTO work = workClient.getWorkById(request.getWorkId());
            if (work == null || work.getId() == null) {
                return ApiResponse.fail(404, "作品不存在");
            }
            CommentResponse response = commentService.addComment(request, userId);
            return ApiResponse.success(response);
        } catch (RuntimeException e) {
            return ApiResponse.fail(500, e.getMessage());
        }
    }

    /**
     * 回复评论
     * 从请求头 User-Id 获取当前用户ID，先调用 work-service 验证作品是否存在
     */
    @PostMapping("/{parentId}/reply")
    public ApiResponse<CommentResponse> replyComment(
            @PathVariable Long parentId,
            @RequestBody ReplyRequest request,
            @RequestHeader("User-Id") Long userId) {
        try {
            // 调用 work-service 验证作品是否存在
            WorkDTO work = workClient.getWorkById(request.getWorkId());
            if (work == null || work.getId() == null) {
                return ApiResponse.fail(404, "作品不存在");
            }
            CommentResponse response = commentService.replyComment(parentId, request, userId);
            return ApiResponse.success(response);
        } catch (RuntimeException e) {
            return ApiResponse.fail(404, e.getMessage());
        }
    }

    /**
     * 按作品查询评论列表
     * 只返回 status=APPROVED 的评论，所有用户可访问
     */
    @GetMapping("/work/{workId}")
    public ApiResponse<List<CommentResponse>> getCommentsByWork(@PathVariable Long workId) {
        List<CommentResponse> comments = commentService.getCommentsByWork(workId);
        return ApiResponse.success(comments);
    }

    /**
     * 点赞/取消点赞评论
     * 从请求头 User-Id 获取当前用户ID，已点赞则取消，未点赞则点赞
     */
    @PutMapping("/{id}/like")
    public ApiResponse<String> toggleLike(
            @PathVariable Long id,
            @RequestHeader("User-Id") Long userId) {
        try {
            boolean liked = commentService.toggleLike(id, userId);
            return ApiResponse.success(liked ? "点赞成功" : "已取消点赞");
        } catch (RuntimeException e) {
            return ApiResponse.fail(404, e.getMessage());
        }
    }

    /**
     * 编辑自己的评论
     * 从请求头 User-Id 获取当前用户ID，校验是否为本人
     */
    @PutMapping("/{id}")
    public ApiResponse<CommentResponse> updateComment(
            @PathVariable Long id,
            @RequestBody UpdateCommentRequest request,
            @RequestHeader("User-Id") Long userId) {
        try {
            CommentResponse response = commentService.updateComment(id, request, userId);
            return ApiResponse.success(response);
        } catch (RuntimeException e) {
            return ApiResponse.fail(403, e.getMessage());
        }
    }

    /**
     * 删除自己的评论
     * 从请求头 User-Id 获取当前用户ID，校验是否为本人
     */
    @DeleteMapping("/{id}")
    public ApiResponse<Void> deleteComment(
            @PathVariable Long id,
            @RequestHeader("User-Id") Long userId) {
        try {
            commentService.deleteComment(id, userId);
            return ApiResponse.success(null);
        } catch (RuntimeException e) {
            return ApiResponse.fail(403, e.getMessage());
        }
    }

    // ==================== 管理员接口 ====================

    /**
     * 管理员：置顶/取消置顶评论
     * 从请求头 User-Role 校验管理员权限
     */
    @PutMapping("/admin/{id}/sticky")
    public ApiResponse<Void> toggleSticky(
            @PathVariable Long id,
            @RequestParam Boolean sticky,
            @RequestHeader("User-Role") String userRole) {
        if (!"ADMIN".equals(userRole)) {
            return ApiResponse.fail(403, "权限不足，仅管理员可操作");
        }
        try {
            commentService.toggleSticky(id, sticky);
            return ApiResponse.success(null);
        } catch (RuntimeException e) {
            return ApiResponse.fail(404, e.getMessage());
        }
    }

    /**
     * 管理员：设为/取消精华评论
     * 从请求头 User-Role 校验管理员权限
     */
    @PutMapping("/admin/{id}/essence")
    public ApiResponse<Void> toggleEssence(
            @PathVariable Long id,
            @RequestParam Boolean essence,
            @RequestHeader("User-Role") String userRole) {
        if (!"ADMIN".equals(userRole)) {
            return ApiResponse.fail(403, "权限不足，仅管理员可操作");
        }
        try {
            commentService.toggleEssence(id, essence);
            return ApiResponse.success(null);
        } catch (RuntimeException e) {
            return ApiResponse.fail(404, e.getMessage());
        }
    }

    /**
     * 管理员：删除任意评论
     * 从请求头 User-Role 校验管理员权限
     */
    @DeleteMapping("/admin/{id}")
    public ApiResponse<Void> adminDeleteComment(
            @PathVariable Long id,
            @RequestHeader("User-Role") String userRole) {
        if (!"ADMIN".equals(userRole)) {
            return ApiResponse.fail(403, "权限不足，仅管理员可操作");
        }
        try {
            commentService.adminDeleteComment(id);
            return ApiResponse.success(null);
        } catch (RuntimeException e) {
            return ApiResponse.fail(404, e.getMessage());
        }
    }

    /**
     * 管理员：审核评论（通过/驳回）
     * 从请求头 User-Role 校验管理员权限
     */
    @PutMapping("/admin/{id}/status")
    public ApiResponse<Void> updateCommentStatus(
            @PathVariable Long id,
            @RequestBody UpdateCommentStatusRequest request,
            @RequestHeader("User-Role") String userRole) {
        if (!"ADMIN".equals(userRole)) {
            return ApiResponse.fail(403, "权限不足，仅管理员可操作");
        }
        try {
            commentService.updateCommentStatus(id, request);
            return ApiResponse.success(null);
        } catch (IllegalArgumentException e) {
            return ApiResponse.fail(400, e.getMessage());
        } catch (RuntimeException e) {
            return ApiResponse.fail(404, e.getMessage());
        }
    }

    /**
     * 管理员：获取待审核评论列表
     * 从请求头 User-Role 校验管理员权限
     */
    @GetMapping("/admin/pending")
    public ApiResponse<List<CommentResponse>> getPendingComments(
            @RequestHeader("User-Role") String userRole) {
        if (!"ADMIN".equals(userRole)) {
            return ApiResponse.fail(403, "权限不足，仅管理员可操作");
        }
        List<CommentResponse> comments = commentService.getPendingComments();
        return ApiResponse.success(comments);
    }

    // ==================== 供 A（work-service）Feign 调用的兼容接口 ====================

    /**
     * 按作品查询评论列表（供 work-service 内部 Feign 调用，兼容旧接口）
     */
    @GetMapping
    public List<com.yn.anime.common.dto.CommentDTO> list(
            @RequestParam String targetType,
            @RequestParam Long targetId) {
        if (!"WORK".equals(targetType)) {
            return List.of();
        }
        List<CommentResponse> comments = commentService.getCommentsByWork(targetId);
        return comments.stream()
                .map(c -> new com.yn.anime.common.dto.CommentDTO(
                        c.getId(), c.getUserId(), "WORK", c.getWorkId(),
                        c.getContent(), c.getLikeCount().longValue()))
                .toList();
    }
}
