package com.yn.anime.comment.service.impl;

import com.yn.anime.comment.dto.*;
import com.yn.anime.comment.entity.Comment;
import com.yn.anime.comment.entity.CommentLike;
import com.yn.anime.comment.repository.CommentLikeRepository;
import com.yn.anime.comment.repository.CommentRepository;
import com.yn.anime.comment.service.CommentService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * 评论服务实现
 */
@Service
public class CommentServiceImpl implements CommentService {

    private final CommentRepository commentRepository;
    private final CommentLikeRepository commentLikeRepository;

    public CommentServiceImpl(CommentRepository commentRepository,
                              CommentLikeRepository commentLikeRepository) {
        this.commentRepository = commentRepository;
        this.commentLikeRepository = commentLikeRepository;
    }

    @Override
    @Transactional
    public CommentResponse addComment(CommentRequest request, Long userId) {
        Comment comment = new Comment(userId, request.getWorkId(), null, request.getContent());
        comment.setStatus("PENDING"); // 新评论默认待审核
        commentRepository.save(comment);
        return toCommentResponse(comment);
    }

    @Override
    @Transactional
    public CommentResponse replyComment(Long parentId, ReplyRequest request, Long userId) {
        // 验证父评论是否存在
        Comment parent = commentRepository.findById(parentId)
                .orElseThrow(() -> new RuntimeException("父评论不存在"));

        Comment reply = new Comment(userId, request.getWorkId(), parentId, request.getContent());
        reply.setStatus("PENDING"); // 回复默认待审核
        commentRepository.save(reply);
        return toCommentResponse(reply);
    }

    @Override
    public List<CommentResponse> getCommentsByWork(Long workId) {
        List<Comment> comments = commentRepository.findByWorkIdAndStatusOrderByCreatedAtDesc(workId, "APPROVED");
        return comments.stream()
                .map(this::toCommentResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public boolean toggleLike(Long commentId, Long userId) {
        Optional<CommentLike> existing = commentLikeRepository.findByCommentIdAndUserId(commentId, userId);
        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new RuntimeException("评论不存在"));

        if (existing.isPresent()) {
            // 取消点赞
            commentLikeRepository.deleteByCommentIdAndUserId(commentId, userId);
            comment.setLikeCount(Math.max(0, comment.getLikeCount() - 1));
            commentRepository.save(comment);
            return false; // 已取消点赞
        } else {
            // 点赞
            CommentLike like = new CommentLike(commentId, userId);
            commentLikeRepository.save(like);
            comment.setLikeCount(comment.getLikeCount() + 1);
            commentRepository.save(comment);
            return true; // 已点赞
        }
    }

    @Override
    @Transactional
    public CommentResponse updateComment(Long commentId, UpdateCommentRequest request, Long userId) {
        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new RuntimeException("评论不存在"));

        // 校验是否为本人操作
        if (!comment.getUserId().equals(userId)) {
            throw new RuntimeException("无权编辑他人的评论");
        }

        comment.setContent(request.getContent());
        comment.setUpdatedAt(LocalDateTime.now());
        // 编辑后重新进入待审核状态
        comment.setStatus("PENDING");
        commentRepository.save(comment);
        return toCommentResponse(comment);
    }

    @Override
    @Transactional
    public void deleteComment(Long commentId, Long userId) {
        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new RuntimeException("评论不存在"));

        // 校验是否为本人操作
        if (!comment.getUserId().equals(userId)) {
            throw new RuntimeException("无权删除他人的评论");
        }

        commentRepository.delete(comment);
    }

    @Override
    @Transactional
    public void toggleSticky(Long commentId, Boolean sticky) {
        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new RuntimeException("评论不存在"));
        comment.setIsSticky(sticky);
        comment.setUpdatedAt(LocalDateTime.now());
        commentRepository.save(comment);
    }

    @Override
    @Transactional
    public void toggleEssence(Long commentId, Boolean essence) {
        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new RuntimeException("评论不存在"));
        comment.setIsEssence(essence);
        comment.setUpdatedAt(LocalDateTime.now());
        commentRepository.save(comment);
    }

    @Override
    @Transactional
    public void adminDeleteComment(Long commentId) {
        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new RuntimeException("评论不存在"));
        commentRepository.delete(comment);
    }

    @Override
    @Transactional
    public void updateCommentStatus(Long commentId, UpdateCommentStatusRequest request) {
        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new RuntimeException("评论不存在"));

        if (!"APPROVED".equals(request.getStatus()) && !"REJECTED".equals(request.getStatus())) {
            throw new IllegalArgumentException("审核状态只能是 APPROVED 或 REJECTED");
        }

        comment.setStatus(request.getStatus());
        comment.setUpdatedAt(LocalDateTime.now());
        commentRepository.save(comment);
    }

    @Override
    public List<CommentResponse> getPendingComments() {
        List<Comment> comments = commentRepository.findByStatusOrderByCreatedAtDesc("PENDING");
        return comments.stream()
                .map(this::toCommentResponse)
                .collect(Collectors.toList());
    }

    /**
     * 实体转响应DTO
     */
    private CommentResponse toCommentResponse(Comment comment) {
        CommentResponse response = new CommentResponse();
        response.setId(comment.getId());
        response.setUserId(comment.getUserId());
        response.setWorkId(comment.getWorkId());
        response.setParentId(comment.getParentId());
        response.setContent(comment.getContent());
        response.setLikeCount(comment.getLikeCount());
        response.setIsSticky(comment.getIsSticky());
        response.setIsEssence(comment.getIsEssence());
        response.setStatus(comment.getStatus());
        response.setCreatedAt(comment.getCreatedAt());
        response.setUpdatedAt(comment.getUpdatedAt());
        return response;
    }
}
