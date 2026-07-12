package com.yn.anime.character.vo;

import lombok.Data;
import com.yn.anime.character.entity.Character;
import java.util.List;

/*
    classDescription:
*/
@Data
public class CharacterDetailVO {

    private Character character;
    // 所属作品
    private List<CharacterWorkVO> works;
    // 角色关系
    private List<CharacterRelationVO> relations;
}
