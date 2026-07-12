package com.yn.anime.rating.repository;

import com.yn.anime.rating.entity.RatingScore;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * 评分记录 Repository
 */
@Repository
public interface RatingScoreRepository extends JpaRepository<RatingScore, Long> {

    /**
     * 根据用户ID和作品ID查找评分记录
     */
    Optional<RatingScore> findByUserIdAndWorkId(Long userId, Long workId);

    /**
     * 根据用户ID查找所有评分
     */
    java.util.List<RatingScore> findByUserId(Long userId);

    /**
     * 根据作品ID查找所有评分
     */
    java.util.List<RatingScore> findByWorkId(Long workId);
}
