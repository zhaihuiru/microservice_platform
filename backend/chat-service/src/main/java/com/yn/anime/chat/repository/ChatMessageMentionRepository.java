package com.yn.anime.chat.repository;

import com.yn.anime.chat.entity.ChatMessageMention;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ChatMessageMentionRepository extends JpaRepository<ChatMessageMention, Long> {
    List<ChatMessageMention> findByMessageId(Long messageId);

    List<ChatMessageMention> findByMentionedUserIdOrderByCreatedAtDesc(Long mentionedUserId);
}