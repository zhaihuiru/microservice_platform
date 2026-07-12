package com.yn.anime.character.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.yn.anime.character.dto.CharacterActorWorkDTO;
import com.yn.anime.character.entity.WorkCharacterActor;
import com.yn.anime.character.vo.WorkCharacterVO;

import java.util.List;

public interface WorkCharacterActorService extends IService<WorkCharacterActor> {

    boolean removeByCompositeKey(Long workId, Long characterId, Long personId);

    WorkCharacterActor getByCompositeKey(Long workId, Long characterId, Long personId);

    List<WorkCharacterActor> listByWorkId(Long workId);

    List<WorkCharacterActor> listByCharacterId(Long characterId);

    Page<WorkCharacterActor> pageRelations(long current, long size, Long workId, Long characterId, Long personId);

    List<CharacterActorWorkDTO> listByPersonId(Long personId);

    List<WorkCharacterVO> listCharactersByWork(Long workId);
}
