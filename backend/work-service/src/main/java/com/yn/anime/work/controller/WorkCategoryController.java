package com.yn.anime.work.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.yn.anime.common.response.ApiResponse;
import com.yn.anime.work.entity.WorkCategory;
import com.yn.anime.work.service.WorkCategoryService;
import com.yn.anime.work.util.RoleUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/work-categories")
@RequiredArgsConstructor
public class WorkCategoryController {

    private final WorkCategoryService workCategoryService;

    @GetMapping
    public ApiResponse<?> list(
            @RequestParam(required = false) Long workId,
            @RequestParam(required = false) Long categoryId,
            @RequestParam(required = false) Long current,
            @RequestParam(required = false) Long size) {
        if (workId != null && categoryId != null && current == null) {
            WorkCategory relation = workCategoryService.getByCompositeKey(workId, categoryId);
            if (relation == null) {
                return ApiResponse.fail(404, "关联不存在");
            }
            return ApiResponse.success(relation);
        }
        if (workId != null && current == null) {
            List<WorkCategory> list = workCategoryService.listByWorkId(workId);
            return ApiResponse.success(list);
        }
        if (categoryId != null && current == null) {
            List<WorkCategory> list = workCategoryService.listByCategoryId(categoryId);
            return ApiResponse.success(list);
        }
        long pageCurrent = current != null ? current : 1L;
        long pageSize = size != null ? size : 10L;
        Page<WorkCategory> page = workCategoryService.pageWorkCategories(pageCurrent, pageSize, workId, categoryId);
        return ApiResponse.success(page);
    }

    @PostMapping
    public ApiResponse<WorkCategory> create(@RequestBody WorkCategory workCategory,
                                            @RequestHeader(
                                                    value = "X-User-Roles",
                                                    required = false)String roles) {
        if (!RoleUtils.isAdmin(roles)) {
            return ApiResponse.fail(
                    403,
                    "无权限"
            );
        }
        workCategoryService.save(workCategory);
        return ApiResponse.success(workCategory);
    }

    @DeleteMapping
    public ApiResponse<Void> delete(
            @RequestParam Long workId,
            @RequestParam Long categoryId,
            @RequestHeader(
                    value = "X-User-Roles",
                    required = false)String roles) {
        if (!RoleUtils.isAdmin(roles)) {
            return ApiResponse.fail(
                    403,
                    "无权限"
            );
        }
        if (!workCategoryService.removeByCompositeKey(workId, categoryId)) {
            return ApiResponse.fail(404, "关联不存在");
        }
        return ApiResponse.success();
    }
}
