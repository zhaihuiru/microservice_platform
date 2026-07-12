package com.yn.anime.work.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.yn.anime.common.response.ApiResponse;
import com.yn.anime.work.entity.Category;
import com.yn.anime.work.entity.Work;
import com.yn.anime.work.service.CategoryService;
import com.yn.anime.work.service.WorkService;
import com.yn.anime.work.util.RoleUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/categories")
@RequiredArgsConstructor
public class CategoryController {

    private final CategoryService categoryService;
    private final WorkService workService;

    @GetMapping("/{id}")
    public ApiResponse<Category> getById(@PathVariable Long id) {
        Category category = categoryService.getById(id);
        if (category == null) {
            return ApiResponse.fail(404, "分类不存在");
        }
        return ApiResponse.success(category);
    }

    // 查询所有分类
    @GetMapping("/all")
    public ApiResponse<List<Category>> all() {
        return ApiResponse.success(
                categoryService.list()
        );
    }

    @PostMapping
    public ApiResponse<Category> create(@RequestBody Category category,
                                        @RequestHeader(
                                                value = "X-User-Roles",
                                                required = false)String roles) {
        if (!RoleUtils.isAdmin(roles)) {
            return ApiResponse.fail(
                    403,
                    "无权限"
            );
        }
        if (categoryService.existsByName(category.getName())) {
            return ApiResponse.fail("该分类名称已存在，请勿重复添加");
        }
        categoryService.save(category);
        return ApiResponse.success(category);
    }

    @PutMapping("/{id}")
    public ApiResponse<Category> update(@PathVariable Long id,
                                        @RequestBody Category category,
                                        @RequestHeader(
                                                value = "X-User-Roles",
                                                required = false)String roles) {
        if (!RoleUtils.isAdmin(roles)) {
            return ApiResponse.fail(
                    403,
                    "无权限"
            );
        }
        category.setId(id);
        if (!categoryService.updateById(category)) {
            return ApiResponse.fail(404, "分类不存在");
        }
        return ApiResponse.success(categoryService.getById(id));
    }

    @DeleteMapping("/{id}")
    public ApiResponse<Void> delete(@PathVariable Long id,
                                    @RequestHeader(
                                            value = "X-User-Roles",
                                            required = false)String roles) {
        if (!RoleUtils.isAdmin(roles)) {
            return ApiResponse.fail(
                    403,
                    "无权限"
            );
        }
        if (!categoryService.removeById(id)) {
            return ApiResponse.fail(404, "分类不存在");
        }
        return ApiResponse.success();
    }

    // 根据分类查询相关作品
    @GetMapping("/{id}/works")
    public ApiResponse<Page<Work>> worksByCategory(
            @PathVariable Long id,
            @RequestParam(defaultValue = "1") long current,
            @RequestParam(defaultValue = "10") long size) {

        return ApiResponse.success(
                workService.pageWorksByCategory(
                        id,
                        current,
                        size
                )
        );
    }
}
