package com.yn.anime.work.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.yn.anime.work.entity.Work;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface WorkMapper extends BaseMapper<Work> {
    Page<Work> pageWorksByCategory(
            Page<Work> page,
            @Param("categoryId") Long categoryId
    );
}
