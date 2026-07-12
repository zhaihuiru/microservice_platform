package com.yn.anime.favorite.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

/**
 * 收藏项实体
 */
@Entity
@Table(name = "fav_item", uniqueConstraints = @UniqueConstraint(name = "uk_folder_work", columnNames = {"folder_id", "work_id"}))
public class FavoriteItem {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "folder_id", nullable = false)
    private Long folderId;

    @Column(name = "work_id", nullable = false)
    private Long workId;

    @Column(name = "created_at")
    private LocalDateTime createdAt = LocalDateTime.now();

    public FavoriteItem() {}

    public FavoriteItem(Long folderId, Long workId) {
        this.folderId = folderId;
        this.workId = workId;
        this.createdAt = LocalDateTime.now();
    }

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public Long getFolderId() { return folderId; }
    public void setFolderId(Long folderId) { this.folderId = folderId; }
    public Long getWorkId() { return workId; }
    public void setWorkId(Long workId) { this.workId = workId; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
}
