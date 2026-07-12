package com.yn.anime.notification.repository;

import com.yn.anime.notification.entity.NoticeUserMessage;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface NoticeUserMessageRepository extends JpaRepository<NoticeUserMessage, Long> {
    Page<NoticeUserMessage> findByReceiverIdAndDeletedFalseOrderByCreatedAtDesc(Long receiverId, Pageable pageable);

    long countByReceiverIdAndReadStatusAndDeletedFalse(Long receiverId, Integer readStatus);

    Optional<NoticeUserMessage> findByIdAndReceiverIdAndDeletedFalse(Long id, Long receiverId);

    List<NoticeUserMessage> findByReceiverIdAndReadStatusAndDeletedFalse(Long receiverId, Integer readStatus);

    List<NoticeUserMessage> findByMessageIdAndDeletedFalse(Long messageId);

    Optional<NoticeUserMessage> findByIdAndDeletedFalse(Long id);
}