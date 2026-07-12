package com.yn.anime.comment.service;

import com.yn.anime.comment.dto.*;

import java.util.List;

/**
 * 评论服务接口
 */
public interface CommentService {

    /**
     * 发表评论
     */
    CommentResponse addComment(CommentRequest request, Long userId);

    /**
     * 回复评论
     */
    CommentResponse replyComment(Long parentId, ReplyRequest request, Long userId);

    /**
     * 按作品查询评论列表（只返回已通过的评论）
     */
    List<CommentResponse> getCommentsByWork(Long workId);

    /**
     * 点赞/取消点赞评论
     */
    boolean toggleLike(Long commentId, Long userId);

    /**
     * 编辑自己的评论
     */
    CommentResponse updateComment(Long commentId, UpdateCommentRequest request, Long userId);

    /**
     * 删除自己的评论
     */
    void deleteComment(Long commentId, Long userId);

    /**
     * 管理员：置顶/取消置顶
     */
    void toggleSticky(Long commentId, Boolean sticky);

    /**
     * 管理员：设为/取消精华
     */
    void toggleEssence(Long commentId, Boolean essence);

    /**
     * 管理员：删除任意评论
     */
    void adminDeleteComment(Long commentId);

    /**
     * 管理员：审核评论（通过/驳回）
     */
    void updateCommentStatus(Long commentId, UpdateCommentStatusRequest request);

    /**
     * 管理员：获取待审核评论列表
     */
    List<CommentResponse> getPendingComments();
}
