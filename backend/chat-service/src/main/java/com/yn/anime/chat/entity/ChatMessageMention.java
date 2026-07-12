package com.yn.anime.chat.entity;

import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
@Table(
        name = "chat_message_mention",
        indexes = {
                @Index(name = "idx_mention_message", columnList = "message_id"),
                @Index(name = "idx_mention_user", columnList = "mentioned_user_id")
        }
)
public class ChatMessageMention {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "message_id", nullable = false)
    private Long messageId;

    @Column(name = "mentioned_user_id", nullable = false)
    private Long mentionedUserId;

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

    public Long getMessageId() {
        return messageId;
    }

    public void setMessageId(Long messageId) {
        this.messageId = messageId;
    }

    public Long getMentionedUserId() {
        return mentionedUserId;
    }

    public void setMentionedUserId(Long mentionedUserId) {
        this.mentionedUserId = mentionedUserId;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
}