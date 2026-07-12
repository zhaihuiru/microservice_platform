package com.yn.anime.rating.service;

import com.yn.anime.rating.dto.*;

/**
 * 评分服务接口
 */
public interface RatingService {

    /**
     * 提交/修改评分
     */
    RatingResponse submitRating(RatingRequest request, Long userId);

    /**
     * 获取作品平均分
     */
    AvgRatingResponse getAvgRating(Long workId);

    /**
     * 获取当前用户对某作品的评分
     */
    RatingResponse getUserRating(Long workId, Long userId);

    /**
     * 删除自己对某作品的评分（按作品ID）
     */
    void deleteRating(Long workId, Long userId);

    /**
     * 管理员删除任意评分记录
     */
    void adminDeleteRating(Long ratingId);

    /**
     * 投票
     */
    void vote(VoteRequest request, Long userId);

    /**
     * 查看投票结果
     */
    VoteResultResponse getVoteResult(Long topicId);
}
