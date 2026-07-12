package com.yn.anime.character.vo;

import lombok.Data;

/*
    classDescription:
*/
@Data
public class CharacterRelationVO {

    private Long characterId;

    private String characterName;

    private String avatarUrl;

    private String relationType;
}