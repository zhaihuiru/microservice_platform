package com.yn.anime.character.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.yn.anime.character.entity.Character;
import com.yn.anime.character.vo.CharacterDetailVO;
import com.yn.anime.character.vo.CharacterWorkVO;

import java.util.List;

public interface CharacterService extends IService<Character> {

    Page<Character> pageCharacters(long current, long size, String name, String gender);

    Page<Character> searchCharacters(
            long current,
            long size,
            String keyword
    );

    CharacterDetailVO getCharacterDetail(
            Long characterId
    );

    List<CharacterWorkVO> listWorks(Long characterId);

    Boolean exists(Long id);

    Character getCharacterWithFullUrl(Long id);
}
