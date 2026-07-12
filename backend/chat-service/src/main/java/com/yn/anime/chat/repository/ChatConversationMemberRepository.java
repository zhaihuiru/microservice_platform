package com.yn.anime.chat.repository;

import com.yn.anime.chat.entity.ChatConversationMember;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface ChatConversationMemberRepository extends JpaRepository<ChatConversationMember, Long> {
    Optional<ChatConversationMember> findByConversationIdAndUserId(Long conversationId, Long userId);

    Optional<ChatConversationMember> findByConversationIdAndUserIdAndDeletedFalse(Long conversationId, Long userId);

    List<ChatConversationMember> findByUserIdAndDeletedFalseOrderByUpdatedAtDesc(Long userId);

    List<ChatConversationMember> findByConversationIdAndDeletedFalse(Long conversationId);

    boolean existsByConversationIdAndUserIdAndDeletedFalse(Long conversationId, Long userId);

    long countByConversationIdAndDeletedFalse(Long conversationId);

    @Query("""
            select m.userId from ChatConversationMember m
            where m.conversationId = :conversationId
              and m.deleted = false
            """)
    List<Long> findActiveUserIdsByConversationId(@Param("conversationId") Long conversationId);

    @Query("""
            select m from ChatConversationMember m
            where m.conversationId = :conversationId
              and m.deleted = false
              and m.muted = true
            """)
    List<ChatConversationMember> findMutedMembers(@Param("conversationId") Long conversationId);
}