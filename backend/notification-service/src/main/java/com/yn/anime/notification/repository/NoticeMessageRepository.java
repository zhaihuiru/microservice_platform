package com.yn.anime.notification.repository;

import com.yn.anime.notification.entity.NoticeMessage;
import org.springframework.data.jpa.repository.JpaRepository;

public interface NoticeMessageRepository extends JpaRepository<NoticeMessage, Long> {
}