package com.yn.anime.auth.repository;

import com.yn.anime.auth.entity.UserOperationLog;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface UserOperationLogRepository extends JpaRepository<UserOperationLog, Long> {
    List<UserOperationLog> findByTargetUserIdOrderByCreatedAtDesc(Long targetUserId);
}