package com.yn.anime.comment.repository;

import com.yn.anime.comment.entity.CommentLike;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

/**
 * 评论点赞 Repository
 */
@Repository
public interface CommentLikeRepository extends JpaRepository<CommentLike, Long> {

    /**
     * 根据评论ID和用户ID查找点赞记录
     */
    Optional<CommentLike> findByCommentIdAndUserId(Long commentId, Long userId);

    /**
     * 根据评论ID统计点赞数
     */
    long countByCommentId(Long commentId);

    /**
     * 根据评论ID和用户ID删除点赞记录
     */
    @Modifying
    @Transactional
    void deleteByCommentIdAndUserId(Long commentId, Long userId);
}
