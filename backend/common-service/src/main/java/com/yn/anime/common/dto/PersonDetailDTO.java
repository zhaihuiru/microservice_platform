package com.yn.anime.common.dto;

import lombok.Data;

import java.time.LocalDate;

/*
    classDescription:
*/
@Data
public class PersonDetailDTO {

    private Long id;

    private String name;

    private String country;

    private LocalDate birthday;

    private String avatarUrl;

    private String introduction;
}