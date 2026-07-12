package com.yn.anime.common.dto;

/*
    classDescription:
*/

import lombok.Data;

@Data
public class CharacterDetailDTO {

    private Long id;

    private String name;

    private String gender;

    private String description;

    private String avatarUrl;
}