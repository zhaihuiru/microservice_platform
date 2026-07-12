package com.yn.anime.character.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.yn.anime.character.entity.CharacterRelation;
import com.yn.anime.character.vo.CharacterRelationVO;

import java.util.List;

public interface CharacterRelationService extends IService<CharacterRelation> {

    Page<CharacterRelation> pageRelations(long current, long size, Long sourceId, Long targetId, String relationType);

    List<CharacterRelationVO> listRelations(Long characterId);
}
