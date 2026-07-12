package com.yn.anime.work.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.yn.anime.work.entity.WorkCategory;

import java.util.List;

public interface WorkCategoryService extends IService<WorkCategory> {

    boolean removeByCompositeKey(Long workId, Long categoryId);

    WorkCategory getByCompositeKey(Long workId, Long categoryId);

    List<WorkCategory> listByWorkId(Long workId);

    List<WorkCategory> listByCategoryId(Long categoryId);

    Page<WorkCategory> pageWorkCategories(long current, long size, Long workId, Long categoryId);
}
