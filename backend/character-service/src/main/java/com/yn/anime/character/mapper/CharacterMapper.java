package com.yn.anime.character.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.yn.anime.character.entity.Character;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface CharacterMapper extends BaseMapper<Character> {
}
