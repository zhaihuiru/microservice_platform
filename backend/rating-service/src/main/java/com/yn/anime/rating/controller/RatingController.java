package com.yn.anime.rating.controller;

import com.yn.anime.common.response.ApiResponse;
import com.yn.anime.rating.dto.*;
import com.yn.anime.rating.service.RatingService;
import org.springframework.web.bind.annotation.*;

/**
 * 评分服务控制器
 * 路径前缀：/api/ratings
 * 投票路径前缀：/api/votes
 */
@RestController
public class RatingController {

    private final RatingService ratingService;

    public RatingController(RatingService ratingService) {
        this.ratingService = ratingService;
    }

    // ==================== 评分相关接口 (/api/ratings) ====================

    @GetMapping("/api/ratings/ping")
    public String ping() {
        return "rating-service is running";
    }

    /**
     * 提交/修改评分
     * 用户对作品打1-10分，如果已存在评分则更新
     */
    @PostMapping("/api/ratings")
    public ApiResponse<RatingResponse> submitRating(
            @RequestBody RatingRequest request,
            @RequestHeader("User-Id") Long userId) {
        try {
            RatingResponse response = ratingService.submitRating(request, userId);
            return ApiResponse.success(response);
        } catch (IllegalArgumentException e) {
            return ApiResponse.fail(400, e.getMessage());
        } catch (Exception e) {
            return ApiResponse.fail(500, e.getMessage());
        }
    }

    /**
     * 获取作品平均分
     * 从 rating_work_stat 表查询统计信息
     */
    @GetMapping("/api/ratings/avg/{workId}")
    public ApiResponse<AvgRatingResponse> getAvgRating(@PathVariable Long workId) {
        AvgRatingResponse response = ratingService.getAvgRating(workId);
        return ApiResponse.success(response);
    }

    /**
     * 获取当前用户对某作品的评分
     * 从请求头 User-Id 获取当前用户ID
     */
    @GetMapping("/api/ratings/work/{workId}")
    public ApiResponse<RatingResponse> getUserRating(
            @PathVariable Long workId,
            @RequestHeader("User-Id") Long userId) {
        RatingResponse response = ratingService.getUserRating(workId, userId);
        if (response == null) {
            return ApiResponse.fail(404, "未找到该评分记录");
        }
        return ApiResponse.success(response);
    }

    /**
     * 删除自己对某作品的评分
     * 按作品ID和用户ID查找并删除，校验评分所属人是否为当前用户
     */
    @DeleteMapping("/api/ratings/{workId}")
    public ApiResponse<Void> deleteRating(
            @PathVariable Long workId,
            @RequestHeader("User-Id") Long userId) {
        try {
            ratingService.deleteRating(workId, userId);
            return ApiResponse.success(null);
        } catch (RuntimeException e) {
            return ApiResponse.fail(403, e.getMessage());
        }
    }

    // ==================== 投票相关接口 (/api/votes) ====================

    /**
     * 投票
     * 每个用户对每个投票主题只能投一次
     */
    @PostMapping("/api/votes")
    public ApiResponse<Void> vote(
            @RequestBody VoteRequest request,
            @RequestHeader("User-Id") Long userId) {
        try {
            ratingService.vote(request, userId);
            return ApiResponse.success(null);
        } catch (RuntimeException e) {
            return ApiResponse.fail(400, e.getMessage());
        }
    }

    /**
     * 查看投票结果
     * 统计各选项的票数
     */
    @GetMapping("/api/votes/topic/{topicId}/result")
    public ApiResponse<VoteResultResponse> getVoteResult(@PathVariable Long topicId) {
        VoteResultResponse response = ratingService.getVoteResult(topicId);
        return ApiResponse.success(response);
    }

    // ==================== 管理员接口 ====================

    /**
     * 管理员：删除任意评分记录
     * 从请求头 User-Role 校验管理员权限
     */
    @DeleteMapping("/api/ratings/admin/{id}")
    public ApiResponse<Void> adminDeleteRating(
            @PathVariable Long id,
            @RequestHeader("User-Role") String userRole) {
        if (!"ADMIN".equals(userRole)) {
            return ApiResponse.fail(403, "权限不足，仅管理员可操作");
        }
        try {
            ratingService.adminDeleteRating(id);
            return ApiResponse.success(null);
        } catch (RuntimeException e) {
            return ApiResponse.fail(404, e.getMessage());
        }
    }

    // ==================== 供 A（work-service）Feign 调用的兼容接口 ====================

    /**
     * 获取作品评分统计（供 work-service 内部 Feign 调用）
     */
    @GetMapping("/api/ratings/works/{workId}/stat")
    public com.yn.anime.common.dto.RatingStatDTO getWorkRatingStat(@PathVariable Long workId) {
        AvgRatingResponse avg = ratingService.getAvgRating(workId);
        return new com.yn.anime.common.dto.RatingStatDTO(
                workId,
                avg.getAvgScore() != null ? avg.getAvgScore().doubleValue() : 0.0,
                avg.getRatingCount() != null ? avg.getRatingCount().longValue() : 0L,
                0.0);
    }
}
