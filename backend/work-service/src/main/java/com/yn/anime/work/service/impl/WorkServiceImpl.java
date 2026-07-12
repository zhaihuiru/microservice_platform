package com.yn.anime.work.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.yn.anime.work.entity.Category;
import com.yn.anime.work.entity.Work;
import com.yn.anime.work.entity.WorkCategory;
import com.yn.anime.work.mapper.CategoryMapper;
import com.yn.anime.work.mapper.WorkCategoryMapper;
import com.yn.anime.work.mapper.WorkMapper;
import com.yn.anime.work.service.WorkService;
import com.yn.anime.work.vo.WorkDetailVO;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.List;

@Service
@RequiredArgsConstructor
public class WorkServiceImpl extends ServiceImpl<WorkMapper, Work> implements WorkService {

    private final WorkCategoryMapper workCategoryMapper;
    private final CategoryMapper categoryMapper;

    // 注入图片网络访问前缀
    @Value("${file.access-prefix}")
    private String accessPrefix;

    @Override
    public Page<Work> pageWorks(long current, long size, String title, String status) {
        LambdaQueryWrapper<Work> wrapper = new LambdaQueryWrapper<>();
        wrapper.like(StringUtils.hasText(title), Work::getTitle, title)
                .eq(StringUtils.hasText(status), Work::getStatus, status)
                .orderByDesc(Work::getCreatedTime);
        Page<Work> resultPage = page(new Page<>(current, size), wrapper);

        // 遍历分页结果，动态拼接图片链接
        if (resultPage.getRecords() != null) {
            resultPage.getRecords().forEach(item -> {
                if (item != null && StringUtils.hasText(item.getCoverUrl())) {
                    if (!item.getCoverUrl().startsWith("http://") && !item.getCoverUrl().startsWith("https://")) {
                        item.setCoverUrl(accessPrefix + item.getCoverUrl());
                    }
                }
            });
        }
        return resultPage;
    }

    @Override
    public Page<Work> searchWorks(long current, long size, String keyword) {

        LambdaQueryWrapper<Work> wrapper = Wrappers.lambdaQuery();
        wrapper.like(StringUtils.hasText(keyword), Work::getTitle, keyword);

        Page<Work> resultPage = page(new Page<>(current, size), wrapper);

        // 👈 改成这样
        if (resultPage.getRecords() != null) {
            resultPage.getRecords().forEach(item -> {
                if (item != null && StringUtils.hasText(item.getCoverUrl())) {
                    if (!item.getCoverUrl().startsWith("http://") && !item.getCoverUrl().startsWith("https://")) {
                        item.setCoverUrl(accessPrefix + item.getCoverUrl());
                    }
                }
            });
        }
        return resultPage;
    }

    @Override
    public Page<Work> pageWorksByCategory(Long categoryId, long current, long size) {
        Page<Work> page = new Page<>(current, size);
        Page<Work> resultPage = baseMapper.pageWorksByCategory(page, categoryId);

        // 👈 改成这样
        if (resultPage.getRecords() != null) {
            resultPage.getRecords().forEach(item -> {
                if (item != null && StringUtils.hasText(item.getCoverUrl())) {
                    if (!item.getCoverUrl().startsWith("http://") && !item.getCoverUrl().startsWith("https://")) {
                        item.setCoverUrl(accessPrefix + item.getCoverUrl());
                    }
                }
            });
        }
        return resultPage;
    }

    @Override
    public WorkDetailVO getWorkDetail(Long workId) {

        Work work = getById(workId);

        if (work == null) {
            return null;
        }

        if (StringUtils.hasText(work.getCoverUrl())) {
            if (!work.getCoverUrl().startsWith("http://") && !work.getCoverUrl().startsWith("https://")) {
                work.setCoverUrl(accessPrefix + work.getCoverUrl());
            }
        }

        WorkDetailVO vo = new WorkDetailVO();

        vo.setWork(work);

        List<Long> categoryIds =
                workCategoryMapper.selectList(
                                Wrappers.<WorkCategory>lambdaQuery()
                                        .eq(
                                                WorkCategory::getWorkId,
                                                workId
                                        )
                        )
                        .stream()
                        .map(WorkCategory::getCategoryId)
                        .toList();

        if (categoryIds.isEmpty()) {
            vo.setCategories(List.of());
            return vo;
        }

        List<Category> categories = categoryMapper.selectBatchIds(categoryIds);

        vo.setCategories(categories);

        return vo;
    }

    @Override
    public boolean exists(Long id) {

        return lambdaQuery()
                .eq(Work::getId, id)
                .exists();
    }

    @Override
    public Work getWorkWithFullUrl(Long id) {
        // 调用 MP 原生的底层查询方法
        Work work = this.getById(id);
        // 如果查到了数据，并且图片字段不为空，则开始处理 URL
        if (work != null && StringUtils.hasText(work.getCoverUrl())) {
            String url = work.getCoverUrl();
            // 防御性判断：如果数据库里已经是完整的网络路径了，就不重复拼接了
            if (!url.startsWith("http://") && !url.startsWith("https://")) {
                work.setCoverUrl(accessPrefix + url);
            }
        }
        return work;
    }
}
