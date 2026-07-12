package com.yn.anime.chat.entity;

import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
@Table(
        name = "chat_conversation_member",
        uniqueConstraints = {
                @UniqueConstraint(
                        name = "uk_conversation_user",
                        columnNames = {"conversation_id", "user_id"}
                )
        },
        indexes = {
                @Index(name = "idx_member_conversation", columnList = "conversation_id"),
                @Index(name = "idx_member_user", columnList = "user_id"),
                @Index(name = "idx_member_deleted", columnList = "deleted")
        }
)
public class ChatConversationMember {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "conversation_id", nullable = false)
    private Long conversationId;

    @Column(name = "user_id", nullable = false)
    private Long userId;

    /**
     * OWNER 群主
     * ADMIN 群管理员
     * MEMBER 普通成员
     */
    @Column(name = "member_role", nullable = false, length = 32)
    private String memberRole;

    /**
     * 是否被禁言
     */
    @Column(name = "muted", nullable = false)
    private Boolean muted;

    /**
     * 是否已退出 / 被移除
     */
    @Column(name = "deleted", nullable = false)
    private Boolean deleted;

    /**
     * 最后已读消息 ID，用于计算未读数
     */
    @Column(name = "last_read_message_id")
    private Long lastReadMessageId;

    @Column(name = "joined_at", nullable = false)
    private LocalDateTime joinedAt;

    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    @PrePersist
    public void prePersist() {
        LocalDateTime now = LocalDateTime.now();

        if (memberRole == null || memberRole.isBlank()) {
            memberRole = "MEMBER";
        }

        if (muted == null) {
            muted = false;
        }

        if (deleted == null) {
            deleted = false;
        }

        if (joinedAt == null) {
            joinedAt = now;
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

    public Long getConversationId() {
        return conversationId;
    }

    public void setConversationId(Long conversationId) {
        this.conversationId = conversationId;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public String getMemberRole() {
        return memberRole;
    }

    public void setMemberRole(String memberRole) {
        this.memberRole = memberRole;
    }

    public Boolean getMuted() {
        return muted;
    }

    public void setMuted(Boolean muted) {
        this.muted = muted;
    }

    public Boolean getDeleted() {
        return deleted;
    }

    public void setDeleted(Boolean deleted) {
        this.deleted = deleted;
    }

    public Long getLastReadMessageId() {
        return lastReadMessageId;
    }

    public void setLastReadMessageId(Long lastReadMessageId) {
        this.lastReadMessageId = lastReadMessageId;
    }

    public LocalDateTime getJoinedAt() {
        return joinedAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }
}