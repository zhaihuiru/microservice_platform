package com.yn.anime.chat.entity;

import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
@Table(
        name = "chat_conversation",
        indexes = {
                @Index(name = "idx_conversation_type", columnList = "conversation_type"),
                @Index(name = "idx_work_id", columnList = "work_id"),
                @Index(name = "idx_owner_id", columnList = "owner_id"),
                @Index(name = "idx_status", columnList = "status")
        }
)
public class ChatConversation {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * PRIVATE 私聊
     * GROUP 普通群聊
     * WORK_DISCUSSION 作品讨论群
     */
    @Column(name = "conversation_type", nullable = false, length = 32)
    private String conversationType;

    @Column(name = "title", length = 100)
    private String title;

    /**
     * 作品讨论群关联作品 ID。
     * 普通私聊 / 群聊可以为空。
     */
    @Column(name = "work_id")
    private Long workId;

    @Column(name = "owner_id", nullable = false)
    private Long ownerId;

    /**
     * 0 正常
     * 1 关闭
     * 2 解散
     */
    @Column(name = "status", nullable = false)
    private Integer status;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    @PrePersist
    public void prePersist() {
        LocalDateTime now = LocalDateTime.now();

        if (status == null) {
            status = 0;
        }

        if (createdAt == null) {
            createdAt = now;
        }

        if (updatedAt == null) {
            updatedAt = now;
        }
    }

    @PreUpdate
    public void preUpdate() {
        updatedAt = LocalDateTime.now();
    }

    public Long getId() {
        return id;
    }

    public String getConversationType() {
        return conversationType;
    }

    public void setConversationType(String conversationType) {
        this.conversationType = conversationType;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public Long getWorkId() {
        return workId;
    }

    public void setWorkId(Long workId) {
        this.workId = workId;
    }

    public Long getOwnerId() {
        return ownerId;
    }

    public void setOwnerId(Long ownerId) {
        this.ownerId = ownerId;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }
}