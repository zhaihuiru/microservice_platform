package com.yn.anime.chat.repository;

import com.yn.anime.chat.entity.ChatModerationLog;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ChatModerationLogRepository extends JpaRepository<ChatModerationLog, Long> {
    Page<ChatModerationLog> findAllByOrderByCreatedAtDesc(Pageable pageable);

    Page<ChatModerationLog> findByConversationIdOrderByCreatedAtDesc(Long conversationId, Pageable pageable);

    Page<ChatModerationLog> findByOperatorIdOrderByCreatedAtDesc(Long operatorId, Pageable pageable);

    Page<ChatModerationLog> findByTargetUserIdOrderByCreatedAtDesc(Long targetUserId, Pageable pageable);
}