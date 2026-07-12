package com.yn.anime.rating.repository;

import com.yn.anime.rating.entity.RatingWorkStat;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * 作品评分统计 Repository
 */
@Repository
public interface RatingWorkStatRepository extends JpaRepository<RatingWorkStat, Long> {

    /**
     * 根据作品ID查找统计信息
     */
    Optional<RatingWorkStat> findByWorkId(Long workId);
}
