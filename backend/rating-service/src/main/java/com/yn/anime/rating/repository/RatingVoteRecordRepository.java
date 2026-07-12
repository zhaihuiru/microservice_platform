package com.yn.anime.rating.repository;

import com.yn.anime.rating.entity.RatingVoteRecord;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * 投票记录 Repository
 */
@Repository
public interface RatingVoteRecordRepository extends JpaRepository<RatingVoteRecord, Long> {

    /**
     * 根据用户ID和投票主题ID查找投票记录
     */
    Optional<RatingVoteRecord> findByUserIdAndTopicId(Long userId, Long topicId);

    /**
     * 根据投票主题ID统计各选项票数
     */
    List<RatingVoteRecord> findByTopicId(Long topicId);
}
