package com.yn.anime.character.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.yn.anime.character.client.PersonClient;
import com.yn.anime.character.dto.CharacterActorWorkDTO;
import com.yn.anime.character.entity.WorkCharacterActor;
import com.yn.anime.character.mapper.CharacterMapper;
import com.yn.anime.character.mapper.WorkCharacterActorMapper;
import com.yn.anime.character.service.WorkCharacterActorService;
import com.yn.anime.character.vo.WorkCharacterVO;
import com.yn.anime.common.dto.PersonSimpleDTO;
import com.yn.anime.character.entity.Character;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
public class WorkCharacterActorServiceImpl extends ServiceImpl<WorkCharacterActorMapper, WorkCharacterActor>
        implements WorkCharacterActorService {

    @Resource
    private CharacterMapper characterMapper;

    @Resource
    private PersonClient personFeignClient;

    @Override
    public boolean removeByCompositeKey(Long workId, Long characterId, Long personId) {
        LambdaQueryWrapper<WorkCharacterActor> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(WorkCharacterActor::getWorkId, workId)
                .eq(WorkCharacterActor::getCharacterId, characterId)
                .eq(WorkCharacterActor::getPersonId, personId);
        return remove(wrapper);
    }

    @Override
    public WorkCharacterActor getByCompositeKey(Long workId, Long characterId, Long personId) {
        LambdaQueryWrapper<WorkCharacterActor> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(WorkCharacterActor::getWorkId, workId)
                .eq(WorkCharacterActor::getCharacterId, characterId)
                .eq(WorkCharacterActor::getPersonId, personId);
        return getOne(wrapper);
    }

    @Override
    public List<WorkCharacterActor> listByWorkId(Long workId) {
        LambdaQueryWrapper<WorkCharacterActor> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(WorkCharacterActor::getWorkId, workId);
        return list(wrapper);
    }

    @Override
    public List<WorkCharacterActor> listByCharacterId(Long characterId) {
        LambdaQueryWrapper<WorkCharacterActor> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(WorkCharacterActor::getCharacterId, characterId);
        return list(wrapper);
    }

    @Override
    public List<CharacterActorWorkDTO> listByPersonId(Long personId) {
        return lambdaQuery()
                .eq(
                        WorkCharacterActor::getPersonId,
                        personId
                )
                .list()
                .stream()
                .map(item -> {


                    CharacterActorWorkDTO dto =
                            new CharacterActorWorkDTO();


                    dto.setWorkId(
                            item.getWorkId()
                    );


                    dto.setCharacterId(
                            item.getCharacterId()
                    );


                    dto.setPersonId(
                            item.getPersonId()
                    );


                    dto.setRoleType(
                            item.getRoleType()
                    );


                    return dto;

                })
                .toList();

    }

    @Override
    public Page<WorkCharacterActor> pageRelations(long current, long size, Long workId, Long characterId, Long personId) {
        LambdaQueryWrapper<WorkCharacterActor> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(workId != null, WorkCharacterActor::getWorkId, workId)
                .eq(characterId != null, WorkCharacterActor::getCharacterId, characterId)
                .eq(personId != null, WorkCharacterActor::getPersonId, personId);
        return page(new Page<>(current, size), wrapper);
    }

    @Override
    public List<WorkCharacterVO> listCharactersByWork(
            Long workId) {

        List<WorkCharacterActor> relations =
                listByWorkId(workId);

        if (relations.isEmpty()) {
            return List.of();
        }

        List<Long> characterIds =
                relations.stream()
                        .map(
                                WorkCharacterActor::getCharacterId
                        )
                        .distinct()
                        .toList();

        List<Character> characters =
                characterMapper.selectBatchIds(
                        characterIds
                );

        Map<Long, Character> characterMap =
                characters.stream()
                        .collect(Collectors.toMap(
                                Character::getId,
                                Function.identity()
                        ));

        List<Long> personIds =
                relations.stream()
                        .map(
                                WorkCharacterActor::getPersonId
                        )
                        .distinct()
                        .toList();

        List<PersonSimpleDTO> persons =
                personFeignClient.batch(
                        personIds
                );

        Map<Long, PersonSimpleDTO> personMap =
                persons.stream()
                        .collect(Collectors.toMap(
                                PersonSimpleDTO::getId,
                                Function.identity()
                        ));

        return relations.stream()
                .map(r -> {

                    WorkCharacterVO vo =
                            new WorkCharacterVO();

                    Character character =
                            characterMap.get(
                                    r.getCharacterId()
                            );

                    if(character != null){

                        vo.setCharacterId(
                                character.getId()
                        );

                        vo.setCharacterName(
                                character.getName()
                        );
                    }

                    vo.setRoleType(
                            r.getRoleType()
                    );

                    vo.setPersonId(
                            r.getPersonId()
                    );

                    vo.setActor(
                            personMap.get(
                                    r.getPersonId()
                            )
                    );

                    return vo;
                })
                .toList();
    }
}
