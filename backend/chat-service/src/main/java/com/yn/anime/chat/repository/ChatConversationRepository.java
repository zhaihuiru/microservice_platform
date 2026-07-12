package com.yn.anime.chat.repository;

import com.yn.anime.chat.entity.ChatConversation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface ChatConversationRepository extends JpaRepository<ChatConversation, Long> {
    Page<ChatConversation> findByStatusOrderByCreatedAtDesc(Integer status, Pageable pageable);

    Page<ChatConversation> findAllByOrderByCreatedAtDesc(Pageable pageable);

    List<ChatConversation> findByOwnerIdOrderByCreatedAtDesc(Long ownerId);

    Optional<ChatConversation> findByConversationTypeAndWorkIdAndStatus(
            String conversationType,
            Long workId,
            Integer status
    );

    @Query("""
            select c from ChatConversation c
            where c.conversationType = 'PRIVATE'
              and c.status = 0
              and exists (
                    select 1 from ChatConversationMember m1
                    where m1.conversationId = c.id
                      and m1.userId = :userId1
                      and m1.deleted = false
              )
              and exists (
                    select 1 from ChatConversationMember m2
                    where m2.conversationId = c.id
                      and m2.userId = :userId2
                      and m2.deleted = false
              )
            order by c.createdAt desc
            """)
    List<ChatConversation> findPrivateConversationBetween(
            @Param("userId1") Long userId1,
            @Param("userId2") Long userId2
    );
}