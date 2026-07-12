package com.yn.anime.notification.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(
        name = "notice_user_message",
        indexes = {
                @Index(name = "idx_receiver_read", columnList = "receiver_id, read_status"),
                @Index(name = "idx_receiver_created", columnList = "receiver_id, created_at")
        }
)
public class NoticeUserMessage {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * 关联 notice_message.id
     */
    @Column(name = "message_id", nullable = false)
    private Long messageId;

    /**
     * 接收人 ID
     */
    @Column(name = "receiver_id", nullable = false)
    private Long receiverId;

    /**
     * 0 未读
     * 1 已读
     */
    @Column(name = "read_status")
    private Integer readStatus = 0;

    @Column(name = "read_at")
    private LocalDateTime readAt;

    /**
     * 用户删除通知时，只对自己不可见，不删除 notice_message
     */
    private Boolean deleted = false;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @PrePersist
    public void prePersist() {
        if (createdAt == null) {
            createdAt = LocalDateTime.now();
        }
        if (readStatus == null) {
            readStatus = 0;
        }
        if (deleted == null) {
            deleted = false;
        }
    }

    public Long getId() {
        return id;
    }

    public Long getMessageId() {
        return messageId;
    }

    public Long getReceiverId() {
        return receiverId;
    }

    public Integer getReadStatus() {
        return readStatus;
    }

    public LocalDateTime getReadAt() {
        return readAt;
    }

    public Boolean getDeleted() {
        return deleted;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public void setMessageId(Long messageId) {
        this.messageId = messageId;
    }

    public void setReceiverId(Long receiverId) {
        this.receiverId = receiverId;
    }

    public void setReadStatus(Integer readStatus) {
        this.readStatus = readStatus;
    }

    public void setReadAt(LocalDateTime readAt) {
        this.readAt = readAt;
    }

    public void setDeleted(Boolean deleted) {
        this.deleted = deleted;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
}