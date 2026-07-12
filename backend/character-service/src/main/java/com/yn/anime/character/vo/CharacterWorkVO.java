package com.yn.anime.character.vo;

import com.yn.anime.common.dto.WorkSimpleDTO;
import lombok.Data;

/*
    classDescription:
*/
@Data
public class CharacterWorkVO {

    private Long workId;

    private String roleType;

    private WorkSimpleDTO work;
}