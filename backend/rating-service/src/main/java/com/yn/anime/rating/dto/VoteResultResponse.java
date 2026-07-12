package com.yn.anime.rating.dto;

import java.util.Map;

/**
 * 投票结果 DTO
 */
public class VoteResultResponse {
    private Long topicId;
    private Integer totalVotes;
    private Map<Long, Long> voteCounts;

    public Long getTopicId() { return topicId; }
    public void setTopicId(Long topicId) { this.topicId = topicId; }
    public Integer getTotalVotes() { return totalVotes; }
    public void setTotalVotes(Integer totalVotes) { this.totalVotes = totalVotes; }
    public Map<Long, Long> getVoteCounts() { return voteCounts; }
    public void setVoteCounts(Map<Long, Long> voteCounts) { this.voteCounts = voteCounts; }
}
