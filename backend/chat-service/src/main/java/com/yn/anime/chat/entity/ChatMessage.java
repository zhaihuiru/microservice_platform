package com.yn.anime.chat.entity;

import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
@Table(
        name = "chat_message",
        uniqueConstraints = {
                @UniqueConstraint(
                        name = "uk_message_idempotent",
                        columnNames = {"conversation_id", "sender_id", "client_message_id"}
                )
        },
        indexes = {
                @Index(name = "idx_message_conversation", columnList = "conversation_id"),
                @Index(name = "idx_message_sender", columnList = "sender_id"),
                @Index(name = "idx_message_created", columnList = "created_at"),
                @Index(name = "idx_message_deleted", columnList = "deleted")
        }
)
public class ChatMessage {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "conversation_id", nullable = false)
    private Long conversationId;

    @Column(name = "sender_id", nullable = false)
    private Long senderId;

    /**
     * 前端生成的消息唯一 ID，用于幂等。
     */
    @Column(name = "client_message_id", nullable = false, length = 128)
    private String clientMessageId;

    /**
     * TEXT 文字
     * EMOJI 表情
     * IMAGE 图片
     * SYSTEM 系统消息
     */
    @Column(name = "message_type", nullable = false, length = 32)
    private String messageType;

    @Column(name = "content", columnDefinition = "TEXT")
    private String content;

    @Column(name = "media_url", length = 500)
    private String mediaUrl;

    @Column(name = "reply_to_message_id")
    private Long replyToMessageId;

    @Column(name = "deleted", nullable = false)
    private Boolean deleted;

    /**
     * PASS 正常通过
     * BLOCKED 被敏感词拦截
     */
    @Column(name = "review_status", nullable = false, length = 32)
    private String reviewStatus;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @PrePersist
    public void prePersist() {
        if (deleted == null) {
            deleted = false;
        }

        if (reviewStatus == null || reviewStatus.isBlank()) {
            reviewStatus = "PASS";
        }

        if (createdAt == null) {
            createdAt = LocalDateTime.now();
        }
    }

    public Long getId() {
        return id;
    }

    public Long getConversationId() {
        return conversationId;
    }

    public void setConversationId(Long conversationId) {
        this.conversationId = conversationId;
    }

    public Long getSenderId() {
        return senderId;
    }

    public void setSenderId(Long senderId) {
        this.senderId = senderId;
    }

    public String getClientMessageId() {
        return clientMessageId;
    }

    public void setClientMessageId(String clientMessageId) {
        this.clientMessageId = clientMessageId;
    }

    public String getMessageType() {
        return messageType;
    }

    public void setMessageType(String messageType) {
        this.messageType = messageType;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }


    public String getMediaUrl() {
        return mediaUrl;
    }

    public void setMediaUrl(String mediaUrl) {
        this.mediaUrl = mediaUrl;
    }

    public Long getReplyToMessageId() {
        return replyToMessageId;
    }

    public void setReplyToMessageId(Long replyToMessageId) {
        this.replyToMessageId = replyToMessageId;
    }

    public Boolean getDeleted() {
        return deleted;
    }

    public void setDeleted(Boolean deleted) {
        this.deleted = deleted;
    }

    public String getReviewStatus() {
        return reviewStatus;
    }

    public void setReviewStatus(String reviewStatus) {
        this.reviewStatus = reviewStatus;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
}