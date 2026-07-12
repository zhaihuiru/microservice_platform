package com.yn.anime.character.vo;

import com.yn.anime.common.dto.PersonSimpleDTO;
import lombok.Data;

/*
    classDescription:
*/
@Data
public class WorkCharacterVO {
    private Long characterId;

    private String characterName;

    private String roleType;

    private Long personId;

    private PersonSimpleDTO actor;
}