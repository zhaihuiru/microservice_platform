package com.yn.anime.comment.repository;

import com.yn.anime.comment.entity.Comment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * 评论 Repository
 */
@Repository
public interface CommentRepository extends JpaRepository<Comment, Long> {

    /**
     * 根据作品ID和状态查询评论列表（按创建时间降序）
     */
    List<Comment> findByWorkIdAndStatusOrderByCreatedAtDesc(Long workId, String status);

    /**
     * 根据状态查询评论列表
     */
    List<Comment> findByStatusOrderByCreatedAtDesc(String status);

    /**
     * 根据用户ID查询评论列表
     */
    List<Comment> findByUserIdOrderByCreatedAtDesc(Long userId);

    /**
     * 根据作品ID和父评论ID查询回复列表
     */
    List<Comment> findByWorkIdAndParentIdOrderByCreatedAtAsc(Long workId, Long parentId);
}
