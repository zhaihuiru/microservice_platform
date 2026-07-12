package com.yn.anime.person.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.yn.anime.person.entity.Person;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface PersonMapper extends BaseMapper<Person> {
}
