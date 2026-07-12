package com.yn.anime.work.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.yn.anime.work.entity.WorkCategory;
import com.yn.anime.work.mapper.WorkCategoryMapper;
import com.yn.anime.work.service.WorkCategoryService;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class WorkCategoryServiceImpl extends ServiceImpl<WorkCategoryMapper, WorkCategory> implements WorkCategoryService {

    // 移除某一个作品对应的某一个分类标签（单独解绑一对关系）
    @Override
    public boolean removeByCompositeKey(Long workId, Long categoryId) {
        LambdaQueryWrapper<WorkCategory> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(WorkCategory::getWorkId, workId)
                .eq(WorkCategory::getCategoryId, categoryId);
        return remove(wrapper);
    }

    // 判断「该作品是否已绑定该分类」、编辑前校验关联是否存在；
    @Override
    public WorkCategory getByCompositeKey(Long workId, Long categoryId) {
        LambdaQueryWrapper<WorkCategory> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(WorkCategory::getWorkId, workId)
                .eq(WorkCategory::getCategoryId, categoryId);
        return getOne(wrapper);
    }

    // 打开作品详情时，一次性查出该作品所有分类标签。
    @Override
    public List<WorkCategory> listByWorkId(Long workId) {
        LambdaQueryWrapper<WorkCategory> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(WorkCategory::getWorkId, workId);
        return list(wrapper);
    }

    // 根据分类 ID，查询绑定该分类的所有作品关联记录
    @Override
    public List<WorkCategory> listByCategoryId(Long categoryId) {
        LambdaQueryWrapper<WorkCategory> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(WorkCategory::getCategoryId, categoryId);
        return list(wrapper);
    }

    @Override
    public Page<WorkCategory> pageWorkCategories(long current, long size, Long workId, Long categoryId) {
        LambdaQueryWrapper<WorkCategory> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(workId != null, WorkCategory::getWorkId, workId)
                .eq(categoryId != null, WorkCategory::getCategoryId, categoryId);
        return page(new Page<>(current, size), wrapper);
    }
}
