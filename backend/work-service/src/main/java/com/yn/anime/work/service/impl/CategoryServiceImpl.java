package com.yn.anime.work.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.yn.anime.work.entity.Category;
import com.yn.anime.work.mapper.CategoryMapper;
import com.yn.anime.work.service.CategoryService;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

@Service
public class CategoryServiceImpl extends ServiceImpl<CategoryMapper, Category> implements CategoryService {

    @Override
    public Page<Category> pageCategories(long current, long size, String name) {
        LambdaQueryWrapper<Category> wrapper = new LambdaQueryWrapper<>();
        wrapper.like(StringUtils.hasText(name), Category::getName, name)
                .orderByAsc(Category::getName);
        return page(new Page<>(current, size), wrapper);
    }

    @Override
    public boolean existsByName(String name) {
        LambdaQueryWrapper<Category> wrapper = Wrappers.lambdaQuery();
        wrapper.eq(Category::getName, name);
        return count(wrapper) > 0;
    }
}
