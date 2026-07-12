package com.yn.anime.character.controller;

import com.yn.anime.character.service.CharacterService;
import com.yn.anime.character.entity.Character;
import com.yn.anime.common.dto.CharacterDetailDTO;
import com.yn.anime.common.dto.CharacterSimpleDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.yn.anime.character.dto.CharacterActorWorkDTO;
import com.yn.anime.character.service.WorkCharacterActorService;
import org.springframework.web.bind.annotation.*;


import java.util.List;

/*
    classDescription:
*/
@RestController
@RequestMapping("/internal/character-actors")
@RequiredArgsConstructor
public class InternalCharacterActorController {

    private final WorkCharacterActorService service;
    private final CharacterService characterService;

    /**
     * 根据id获取角色详情
     */
    @GetMapping("/{id}")
    public CharacterDetailDTO getById(
            @PathVariable Long id) {

        Character character = characterService.getById(id);

        if (character == null) {
            return null;
        }

        CharacterDetailDTO dto =
                new CharacterDetailDTO();

        BeanUtils.copyProperties(
                character,
                dto
        );

        return dto;
    }


    /**
     * 获取角色简要信息
     */
    @GetMapping("/simple/{id}")
    public CharacterSimpleDTO simple(
            @PathVariable Long id) {

        Character character =
                characterService.getById(id);

        if (character == null) {
            return null;
        }

        CharacterSimpleDTO dto =
                new CharacterSimpleDTO();

        BeanUtils.copyProperties(
                character,
                dto
        );

        return dto;
    }


    /**
     * 批量查询
     */
    @PostMapping("/batch")
    public List<CharacterSimpleDTO> batch(
            @RequestBody List<Long> ids) {

        return characterService
                .listByIds(ids)
                .stream()
                .map(character -> {

                    CharacterSimpleDTO dto =
                            new CharacterSimpleDTO();

                    BeanUtils.copyProperties(
                            character,
                            dto
                    );

                    return dto;

                })
                .toList();
    }


    /**
     * 判断角色是否存在
     */
    @GetMapping("/exist/{id}")
    public Boolean exist(
            @PathVariable Long id) {

        return characterService.exists(id);
    }



    /**
     * 根据人物id查询参与作品
     *
     * 给person-service调用
     */
    @GetMapping("/person/{personId}/works")
    public List<CharacterActorWorkDTO> listByPerson(
            @PathVariable Long personId
    ){

        return service.listByPersonId(personId);

    }
}
