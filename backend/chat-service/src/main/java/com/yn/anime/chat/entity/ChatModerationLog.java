package com.yn.anime.chat.entity;

import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
@Table(
        name = "chat_moderation_log",
        indexes = {
                @Index(name = "idx_log_operator", columnList = "operator_id"),
                @Index(name = "idx_log_conversation", columnList = "conversation_id"),
                @Index(name = "idx_log_target_user", columnList = "target_user_id"),
                @Index(name = "idx_log_message", columnList = "message_id"),
                @Index(name = "idx_log_operation", columnList = "operation_type")
        }
)
public class ChatModerationLog {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * 管理员 ID
     */
    @Column(name = "operator_id", nullable = false)
    private Long operatorId;

    @Column(name = "conversation_id")
    private Long conversationId;

    @Column(name = "target_user_id")
    private Long targetUserId;

    @Column(name = "message_id")
    private Long messageId;

    /**
     * DELETE_MESSAGE 删除违规消息
     * MUTE_USER 禁言用户
     * UNMUTE_USER 解除禁言
     * CLOSE_CONVERSATION 关闭会话
     * DISBAND_GROUP 解散群聊
     */
    @Column(name = "operation_type", nullable = false, length = 64)
    private String operationType;

    @Column(name = "reason", length = 500)
    private String reason;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @PrePersist
    public void prePersist() {
        if (createdAt == null) {
            createdAt = LocalDateTime.now();
        }
    }

    public Long getId() {
        return id;
    }

    public Long getOperatorId() {
        return operatorId;
    }

    public void setOperatorId(Long operatorId) {
        this.operatorId = operatorId;
    }

    public Long getConversationId() {
        return conversationId;
    }

    public void setConversationId(Long conversationId) {
        this.conversationId = conversationId;
    }

    public Long getTargetUserId() {
        return targetUserId;
    }

    public void setTargetUserId(Long targetUserId) {
        this.targetUserId = targetUserId;
    }

    public Long getMessageId() {
        return messageId;
    }

    public void setMessageId(Long messageId) {
        this.messageId = messageId;
    }

    public String getOperationType() {
        return operationType;
    }

    public void setOperationType(String operationType) {
        this.operationType = operationType;
    }

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
}