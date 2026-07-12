package com.yn.anime.common.dto;

/**
 * 作品信息 DTO（由 work-service 提供，Feign 调用返回）
 */
public class WorkDTO {
    private Long id;
    private String title;
    private String coverUrl;
    private String description;
    private String releaseDate;
    private String status;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }
    public String getCoverUrl() { return coverUrl; }
    public void setCoverUrl(String coverUrl) { this.coverUrl = coverUrl; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    public String getReleaseDate() { return releaseDate; }
    public void setReleaseDate(String releaseDate) { this.releaseDate = releaseDate; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
}
