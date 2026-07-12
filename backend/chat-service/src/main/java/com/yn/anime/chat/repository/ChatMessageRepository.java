package com.yn.anime.chat.repository;

import com.yn.anime.chat.entity.ChatMessage;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ChatMessageRepository extends JpaRepository<ChatMessage, Long> {
    Optional<ChatMessage> findByConversationIdAndSenderIdAndClientMessageId(
            Long conversationId,
            Long senderId,
            String clientMessageId
    );

    Page<ChatMessage> findByConversationIdAndDeletedFalseOrderByCreatedAtAsc(
            Long conversationId,
            Pageable pageable
    );

    Page<ChatMessage> findByConversationIdOrderByCreatedAtDesc(
            Long conversationId,
            Pageable pageable
    );

    List<ChatMessage> findByConversationIdAndIdGreaterThanAndDeletedFalseOrderByCreatedAtAsc(
            Long conversationId,
            Long id
    );

    long countByConversationIdAndIdGreaterThanAndDeletedFalse(
            Long conversationId,
            Long id
    );

    Optional<ChatMessage> findTopByConversationIdAndDeletedFalseOrderByIdDesc(Long conversationId);
}