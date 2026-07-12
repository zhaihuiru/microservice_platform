package com.yn.anime.character.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.yn.anime.character.client.WorkClient;
import com.yn.anime.character.entity.Character;
import com.yn.anime.character.entity.WorkCharacterActor;
import com.yn.anime.character.mapper.CharacterMapper;
import com.yn.anime.character.mapper.WorkCharacterActorMapper;
import com.yn.anime.character.service.CharacterRelationService;
import com.yn.anime.character.service.CharacterService;
import com.yn.anime.character.vo.CharacterDetailVO;
import com.yn.anime.character.vo.CharacterRelationVO;
import com.yn.anime.character.vo.CharacterWorkVO;
import com.yn.anime.common.dto.WorkSimpleDTO;
import jakarta.annotation.Resource;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
public class CharacterServiceImpl extends ServiceImpl<CharacterMapper, Character> implements CharacterService {

    @Resource
    private CharacterRelationService characterRelationService;

    @Resource
    private WorkCharacterActorMapper workCharacterActorMapper;

    @Resource
    private WorkClient workFeignClient;

    @Value("${file.access-prefix}")
    private String accessPrefix;

    @Override
    public Page<Character> pageCharacters(long current, long size, String name, String gender) {
        LambdaQueryWrapper<Character> wrapper = new LambdaQueryWrapper<>();
        wrapper.like(StringUtils.hasText(name), Character::getName, name)
                .eq(StringUtils.hasText(gender), Character::getGender, gender)
                .orderByDesc(Character::getCreatedTime);
        Page<Character> resultPage = page(new Page<>(current, size), wrapper);

        // 内联遍历，动态为角色头像拼接网络前缀
        if (resultPage.getRecords() != null) {
            resultPage.getRecords().forEach(item -> {
                if (item != null && StringUtils.hasText(item.getAvatarUrl())) { // 💡 注意：核对你的字段是 avatarUrl 还是 avatar
                    if (!item.getAvatarUrl().startsWith("http://") && !item.getAvatarUrl().startsWith("https://")) {
                        item.setAvatarUrl(accessPrefix + item.getAvatarUrl());
                    }
                }
            });
        }
        return resultPage;
    }

    // 搜索
    @Override
    public Page<Character> searchCharacters(
            long current,
            long size,
            String keyword) {

        LambdaQueryWrapper<Character> wrapper = Wrappers.lambdaQuery();
        wrapper.like(StringUtils.hasText(keyword), Character::getName, keyword);

        Page<Character> resultPage = page(new Page<>(current, size), wrapper);

        // 👈 3. 搜索结果同样就地循环处理
        if (resultPage.getRecords() != null) {
            resultPage.getRecords().forEach(item -> {
                if (item != null && StringUtils.hasText(item.getAvatarUrl())) {
                    if (!item.getAvatarUrl().startsWith("http://") && !item.getAvatarUrl().startsWith("https://")) {
                        item.setAvatarUrl(accessPrefix + item.getAvatarUrl());
                    }
                }
            });
        }
        return resultPage;
    }
    // 详情
    @Override
    public CharacterDetailVO getCharacterDetail(
            Long characterId) {

        Character character = getById(characterId);

        if (character == null) {
            return null;
        }

        if (StringUtils.hasText(character.getAvatarUrl())) {
            if (!character.getAvatarUrl().startsWith("http://") && !character.getAvatarUrl().startsWith("https://")) {
                character.setAvatarUrl(accessPrefix + character.getAvatarUrl());
            }
        }

        CharacterDetailVO vo = new CharacterDetailVO();

        vo.setCharacter(character);

        // 查询关系
        List<CharacterRelationVO> relations =
                characterRelationService
                        .listRelations(characterId);


        vo.setRelations(
                relations
        );


        /*
        查询作品
     */
        List<CharacterWorkVO> works = listWorks(characterId);
        vo.setWorks(works);
        return vo;
    }

    @Override
    public List<CharacterWorkVO> listWorks(Long characterId) {

        List<WorkCharacterActor> relations =
                workCharacterActorMapper.selectList(
                        Wrappers.<WorkCharacterActor>lambdaQuery()
                                .eq(
                                        WorkCharacterActor::getCharacterId,
                                        characterId
                                )
                );

        if (relations.isEmpty()) {
            return List.of();
        }

        List<Long> workIds =
                relations.stream()
                        .map(WorkCharacterActor::getWorkId)
                        .distinct()
                        .toList();

        List<WorkSimpleDTO> works =
                workFeignClient.batch(workIds);

        Map<Long, WorkSimpleDTO> workMap =
                works.stream()
                        .collect(Collectors.toMap(
                                WorkSimpleDTO::getId,
                                Function.identity()
                        ));

        return relations.stream()
                .map(r -> {

                    CharacterWorkVO vo =
                            new CharacterWorkVO();

                    vo.setWorkId(r.getWorkId());

                    vo.setRoleType(r.getRoleType());

                    vo.setWork(
                            workMap.get(
                                    r.getWorkId()
                            )
                    );

                    return vo;
                })
                .toList();
    }

    @Override
    public Boolean exists(Long id) {

        return lambdaQuery()
                .eq(
                        Character::getId,
                        id
                )
                .exists();
    }

    @Override
    public Character getCharacterWithFullUrl(Long id) {
        Character character = this.getById(id);
        if (character != null && StringUtils.hasText(character.getAvatarUrl())) {
            if (!character.getAvatarUrl().startsWith("http://") && !character.getAvatarUrl().startsWith("https://")) {
                character.setAvatarUrl(accessPrefix + character.getAvatarUrl());
            }
        }
        return character;
    }
}
