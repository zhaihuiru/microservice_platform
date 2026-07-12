package com.yn.anime.notification.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "notice_message")
public class NoticeMessage {
    private Boolean deleted = false;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * 通知标题
     */
    @Column(nullable = false, length = 100)
    private String title;

    /**
     * 通知正文
     */
    @Column(columnDefinition = "TEXT")
    private String content;

    /**
     * SYSTEM / COMMENT / REPLY / ACHIEVEMENT / FAVORITE / RATING / CHAT
     */
    @Column(name = "notice_type", length = 50)
    private String noticeType;

    /**
     * 发送人 ID，系统通知可以为空
     */
    @Column(name = "sender_id")
    private Long senderId;

    /**
     * 关联对象类型，例如 WORK / COMMENT / USER / SYSTEM
     */
    @Column(name = "target_type", length = 50)
    private String targetType;

    /**
     * 关联对象 ID，例如作品 ID、评论 ID
     */
    @Column(name = "target_id")
    private Long targetId;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @PrePersist
    public void prePersist() {
        if (createdAt == null) {
            createdAt = LocalDateTime.now();
        }
        if (noticeType == null || noticeType.isBlank()) {
            noticeType = "SYSTEM";
        }
        if (deleted == null) {
            deleted = false;
        }
    }

    public Boolean getDeleted() {
        return deleted;
    }

    public void setDeleted(Boolean deleted) {
        this.deleted = deleted;
    }

    public Long getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public String getContent() {
        return content;
    }

    public String getNoticeType() {
        return noticeType;
    }

    public Long getSenderId() {
        return senderId;
    }

    public String getTargetType() {
        return targetType;
    }

    public Long getTargetId() {
        return targetId;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public void setNoticeType(String noticeType) {
        this.noticeType = noticeType;
    }

    public void setSenderId(Long senderId) {
        this.senderId = senderId;
    }

    public void setTargetType(String targetType) {
        this.targetType = targetType;
    }

    public void setTargetId(Long targetId) {
        this.targetId = targetId;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
}