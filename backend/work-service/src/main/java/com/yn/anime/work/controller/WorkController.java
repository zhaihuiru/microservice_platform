package com.yn.anime.work.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.yn.anime.common.response.ApiResponse;
import com.yn.anime.work.entity.Work;
import com.yn.anime.work.service.WorkService;
import com.yn.anime.work.util.RoleUtils;
import com.yn.anime.work.vo.WorkDetailVO;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/works")
@RequiredArgsConstructor
public class WorkController {

    private final WorkService workService;

    @GetMapping("/ping")
    public String ping() {
        return "work-service is running";
    }

    @GetMapping("/{id}")
    public ApiResponse<Work> getById(@PathVariable Long id) {
        Work work = workService.getWorkWithFullUrl(id);
        if (work == null) {
            return ApiResponse.fail(404, "作品不存在");
        }
        return ApiResponse.success(work);
    }

    @GetMapping
    public ApiResponse<Page<Work>> page(
            @RequestParam(defaultValue = "1") long current,
            @RequestParam(defaultValue = "10") long size,
            @RequestParam(required = false) String title,
            @RequestParam(required = false) String status) {
        return ApiResponse.success(workService.pageWorks(current, size, title, status));
    }

    @PostMapping
    public ApiResponse<Work> create(@RequestBody Work work, @RequestHeader(
            value = "X-User-Roles",
            required = false)String roles) {

        if (!RoleUtils.isAdmin(roles)) {
            return ApiResponse.fail(
                    403,
                    "无权限"
            );
        }

        workService.save(work);
        return ApiResponse.success(work);
    }

    @PutMapping("/{id}")
    public ApiResponse<Work> update(@PathVariable Long id,
                                    @RequestBody Work work,
                                    @RequestHeader(
                                            value = "X-User-Roles",
                                            required = false)String roles) {
        if (!RoleUtils.isAdmin(roles)) {
            return ApiResponse.fail(
                    403,
                    "无权限"
            );
        }
        work.setId(id);
        if (!workService.updateById(work)) {
            return ApiResponse.fail(404, "作品不存在");
        }
        return ApiResponse.success(workService.getWorkWithFullUrl(id));
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
        if (!workService.removeById(id)) {
            return ApiResponse.fail(404, "作品不存在");
        }
        return ApiResponse.success();
    }
    // 模糊搜索作品
    @GetMapping("/search")
    public ApiResponse<Page<Work>> search(
            @RequestParam String keyword,
            @RequestParam(defaultValue = "1") long current,
            @RequestParam(defaultValue = "10") long size) {

        return ApiResponse.success(
                workService.searchWorks(
                        current,
                        size,
                        keyword
                )
        );
    }

    // 作品详情
    @GetMapping("/{id}/detail")
    public ApiResponse<WorkDetailVO> detail(
            @PathVariable Long id) {

        WorkDetailVO detail =
                workService.getWorkDetail(id);

        if (detail == null) {
            return ApiResponse.fail(404, "作品不存在");
        }

        return ApiResponse.success(detail);
    }
}
