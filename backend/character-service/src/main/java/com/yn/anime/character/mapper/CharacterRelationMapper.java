package com.yn.anime.character.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.yn.anime.character.entity.CharacterRelation;
import com.yn.anime.character.vo.CharacterRelationVO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface CharacterRelationMapper extends BaseMapper<CharacterRelation> {
    @Select("""
        SELECT
            cr.target_id AS targetId,
            c.name AS targetName,
            cr.relation_type AS relationType
        FROM character_relation cr
        JOIN characters c
            ON cr.target_id = c.id
        WHERE cr.source_id = #{characterId}
        """)
    List<CharacterRelationVO> listRelations(
            @Param("characterId") Long characterId
    );
}
