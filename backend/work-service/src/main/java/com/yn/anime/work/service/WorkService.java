package com.yn.anime.work.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.yn.anime.work.entity.Work;
import com.yn.anime.work.vo.WorkDetailVO;

public interface WorkService extends IService<Work> {

    Page<Work> pageWorks(long current, long size, String title, String status);

    // 搜索
    Page<Work> searchWorks(long current, long size, String keyword);

    // 按分类查询
    Page<Work> pageWorksByCategory(Long categoryId, long current, long size);

    // 作品详情
    WorkDetailVO getWorkDetail(Long workId);

    // 判断作品是否存在
    boolean exists(Long id);

    Work getWorkWithFullUrl(Long id);
}
