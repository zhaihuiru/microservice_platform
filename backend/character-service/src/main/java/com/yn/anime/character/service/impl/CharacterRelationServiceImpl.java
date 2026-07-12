package com.yn.anime.character.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.yn.anime.character.entity.CharacterRelation;
import com.yn.anime.character.mapper.CharacterRelationMapper;
import com.yn.anime.character.mapper.CharacterMapper;
import com.yn.anime.character.service.CharacterRelationService;
import com.yn.anime.character.vo.CharacterRelationVO;
import com.yn.anime.character.entity.Character;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import jakarta.annotation.Resource;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
public class CharacterRelationServiceImpl extends ServiceImpl<CharacterRelationMapper, CharacterRelation>
        implements CharacterRelationService {

    @Resource
    private CharacterMapper characterMapper;

    @Override
    public Page<CharacterRelation> pageRelations(long current, long size, Long sourceId, Long targetId, String relationType) {
        LambdaQueryWrapper<CharacterRelation> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(sourceId != null, CharacterRelation::getSourceId, sourceId)
                .eq(targetId != null, CharacterRelation::getTargetId, targetId)
                .eq(StringUtils.hasText(relationType), CharacterRelation::getRelationType, relationType)
                .orderByDesc(CharacterRelation::getId);
        return page(new Page<>(current, size), wrapper);
    }

    @Override
    public List<CharacterRelationVO> listRelations(Long characterId) {

        List<CharacterRelation> relations =
                list(
                        Wrappers.<CharacterRelation>lambdaQuery()
                                .eq(
                                        CharacterRelation::getSourceId,
                                        characterId
                                )
                );

        if (relations.isEmpty()) {
            return List.of();
        }

        List<Long> targetIds =
                relations.stream()
                        .map(
                                CharacterRelation::getTargetId
                        )
                        .distinct()
                        .toList();

        List<Character> characters = characterMapper.selectBatchIds(targetIds);

        Map<Long, Character> characterMap =
                characters.stream()
                        .collect(
                                Collectors.toMap(
                                        Character::getId,
                                        Function.identity()
                                )
                        );

        return relations.stream()
                .map(relation -> {

                    CharacterRelationVO vo =
                            new CharacterRelationVO();

                    Character target =
                            characterMap.get(
                                    relation.getTargetId()
                            );

                    if (target != null) {

                        vo.setCharacterId(
                                target.getId()
                        );

                        vo.setCharacterName(
                                target.getName()
                        );

                        vo.setAvatarUrl(
                                target.getAvatarUrl()
                        );
                    }

                    vo.setRelationType(
                            relation.getRelationType()
                    );

                    return vo;
                })
                .toList();
    }
}
