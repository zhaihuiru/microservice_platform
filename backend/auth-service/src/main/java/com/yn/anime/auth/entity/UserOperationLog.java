package com.yn.anime.auth.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "user_operation_log")
public class UserOperationLog {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "operator_id")
    private Long operatorId;

    @Column(name = "target_user_id")
    private Long targetUserId;

    @Column(name = "operation_type", length = 50)
    private String operationType;

    @Column(length = 255)
    private String description;

    @Column(name = "request_id", length = 100)
    private String requestId;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @PrePersist
    public void prePersist() {
        if (createdAt == null) createdAt = LocalDateTime.now();
    }

    public Long getId() {
        return id;
    }

    public Long getOperatorId() {
        return operatorId;
    }

    public Long getTargetUserId() {
        return targetUserId;
    }

    public String getOperationType() {
        return operationType;
    }

    public String getDescription() {
        return description;
    }

    public String getRequestId() {
        return requestId;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public void setOperatorId(Long operatorId) {
        this.operatorId = operatorId;
    }

    public void setTargetUserId(Long targetUserId) {
        this.targetUserId = targetUserId;
    }

    public void setOperationType(String operationType) {
        this.operationType = operationType;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setRequestId(String requestId) {
        this.requestId = requestId;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
}