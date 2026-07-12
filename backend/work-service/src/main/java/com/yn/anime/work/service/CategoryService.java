package com.yn.anime.work.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.yn.anime.work.entity.Category;

public interface CategoryService extends IService<Category> {

    Page<Category> pageCategories(long current, long size, String name);

    public boolean existsByName(String name);
}
