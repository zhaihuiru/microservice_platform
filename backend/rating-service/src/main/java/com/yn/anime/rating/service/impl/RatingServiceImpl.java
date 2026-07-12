package com.yn.anime.rating.service.impl;

import com.yn.anime.rating.dto.*;
import com.yn.anime.rating.entity.RatingScore;
import com.yn.anime.rating.entity.RatingVoteRecord;
import com.yn.anime.rating.entity.RatingWorkStat;
import com.yn.anime.rating.repository.RatingScoreRepository;
import com.yn.anime.rating.repository.RatingVoteRecordRepository;
import com.yn.anime.rating.repository.RatingWorkStatRepository;
import com.yn.anime.rating.service.RatingService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * 评分服务实现
 */
@Service
public class RatingServiceImpl implements RatingService {

    private final RatingScoreRepository ratingScoreRepository;
    private final RatingWorkStatRepository ratingWorkStatRepository;
    private final RatingVoteRecordRepository ratingVoteRecordRepository;

    public RatingServiceImpl(RatingScoreRepository ratingScoreRepository,
                             RatingWorkStatRepository ratingWorkStatRepository,
                             RatingVoteRecordRepository ratingVoteRecordRepository) {
        this.ratingScoreRepository = ratingScoreRepository;
        this.ratingWorkStatRepository = ratingWorkStatRepository;
        this.ratingVoteRecordRepository = ratingVoteRecordRepository;
    }

    @Override
    @Transactional
    public RatingResponse submitRating(RatingRequest request, Long userId) {
        // 验证评分范围
        if (request.getScore() < 1 || request.getScore() > 10) {
            throw new IllegalArgumentException("评分必须在1-10分之间");
        }

        // 查找是否已有评分记录
        Optional<RatingScore> existing = ratingScoreRepository.findByUserIdAndWorkId(userId, request.getWorkId());
        RatingScore rating;
        boolean isNew = existing.isEmpty();

        if (isNew) {
            rating = new RatingScore(userId, request.getWorkId(), request.getScore());
        } else {
            rating = existing.get();
            rating.setScore(request.getScore());
            rating.setUpdatedAt(java.time.LocalDateTime.now());
        }
        ratingScoreRepository.save(rating);

        // 更新作品评分统计
        updateWorkStat(request.getWorkId());

        return toRatingResponse(rating);
    }

    @Override
    public AvgRatingResponse getAvgRating(Long workId) {
        Optional<RatingWorkStat> stat = ratingWorkStatRepository.findByWorkId(workId);
        AvgRatingResponse response = new AvgRatingResponse();
        response.setWorkId(workId);
        if (stat.isPresent()) {
            response.setAvgScore(stat.get().getAvgScore());
            response.setRatingCount(stat.get().getRatingCount());
        } else {
            response.setAvgScore(BigDecimal.ZERO);
            response.setRatingCount(0);
        }
        return response;
    }

    @Override
    public RatingResponse getUserRating(Long workId, Long userId) {
        Optional<RatingScore> rating = ratingScoreRepository.findByUserIdAndWorkId(userId, workId);
        if (rating.isEmpty()) {
            return null;
        }
        return toRatingResponse(rating.get());
    }

    @Override
    @Transactional
    public void deleteRating(Long workId, Long userId) {
        RatingScore rating = ratingScoreRepository.findByUserIdAndWorkId(userId, workId)
                .orElseThrow(() -> new RuntimeException("未找到该评分记录"));

        ratingScoreRepository.delete(rating);

        // 更新作品评分统计
        updateWorkStat(workId);
    }

    @Override
    @Transactional
    public void adminDeleteRating(Long ratingId) {
        RatingScore rating = ratingScoreRepository.findById(ratingId)
                .orElseThrow(() -> new RuntimeException("评分记录不存在"));

        Long workId = rating.getWorkId();
        ratingScoreRepository.delete(rating);

        // 更新作品评分统计
        updateWorkStat(workId);
    }

    @Override
    @Transactional
    public void vote(VoteRequest request, Long userId) {
        // 检查是否已投票
        Optional<RatingVoteRecord> existing = ratingVoteRecordRepository.findByUserIdAndTopicId(userId, request.getTopicId());
        if (existing.isPresent()) {
            throw new RuntimeException("您已为该主题投过票，不能重复投票");
        }

        RatingVoteRecord record = new RatingVoteRecord(userId, request.getTopicId(), request.getTargetId());
        ratingVoteRecordRepository.save(record);
    }

    @Override
    public VoteResultResponse getVoteResult(Long topicId) {
        List<RatingVoteRecord> records = ratingVoteRecordRepository.findByTopicId(topicId);

        Map<Long, Long> voteCounts = records.stream()
                .collect(Collectors.groupingBy(RatingVoteRecord::getTargetId, Collectors.counting()));

        VoteResultResponse response = new VoteResultResponse();
        response.setTopicId(topicId);
        response.setTotalVotes(records.size());
        response.setVoteCounts(voteCounts);
        return response;
    }

    /**
     * 更新作品评分统计（重新计算平均分）
     */
    private void updateWorkStat(Long workId) {
        List<RatingScore> scores = ratingScoreRepository.findByWorkId(workId);
        RatingWorkStat stat = ratingWorkStatRepository.findByWorkId(workId)
                .orElseGet(() -> new RatingWorkStat(workId));

        if (scores.isEmpty()) {
            stat.setAvgScore(BigDecimal.ZERO);
            stat.setRatingCount(0);
        } else {
            int totalScore = scores.stream().mapToInt(RatingScore::getScore).sum();
            BigDecimal avg = BigDecimal.valueOf(totalScore)
                    .divide(BigDecimal.valueOf(scores.size()), 2, RoundingMode.HALF_UP);
            stat.setAvgScore(avg);
            stat.setRatingCount(scores.size());
        }
        stat.setUpdatedAt(java.time.LocalDateTime.now());
        ratingWorkStatRepository.save(stat);
    }

    /**
     * 实体转响应DTO
     */
    private RatingResponse toRatingResponse(RatingScore rating) {
        RatingResponse response = new RatingResponse();
        response.setId(rating.getId());
        response.setUserId(rating.getUserId());
        response.setWorkId(rating.getWorkId());
        response.setScore(rating.getScore());
        response.setCreatedAt(rating.getCreatedAt());
        response.setUpdatedAt(rating.getUpdatedAt());
        return response;
    }
}
