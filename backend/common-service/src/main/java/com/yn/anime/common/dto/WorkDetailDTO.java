package com.yn.anime.common.dto;

import lombok.Data;

/*
    classDescription:
*/
@Data
public class WorkDetailDTO {

    private Long id;

    private String title;

    private String coverUrl;

    private String description;

    private String status;
}