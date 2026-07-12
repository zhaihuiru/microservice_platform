package com.yn.anime.person.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.yn.anime.common.response.ApiResponse;
import com.yn.anime.person.entity.PersonWork;
import com.yn.anime.person.service.PersonWorkService;
import com.yn.anime.person.util.RoleUtils;
import com.yn.anime.person.vo.WorkPersonVO;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/person-works")
@RequiredArgsConstructor
public class PersonWorkController {

    private final PersonWorkService personWorkService;

    @PostMapping
    public ApiResponse<PersonWork> create(
            @RequestBody PersonWork personWork,
            @RequestHeader(
                    value = "X-User-Roles",
                    required = false)String roles) {

        if (!RoleUtils.isAdmin(roles)) {
            return ApiResponse.fail(
                    403,
                    "无权限"
            );
        }
        personWorkService.createRelation(
                personWork
        );

        return ApiResponse.success(
                personWork
        );
    }

    @DeleteMapping
    public ApiResponse<Void> delete(
            @RequestParam Long personId,
            @RequestParam Long workId,
            @RequestParam String roleType,
            @RequestHeader(
                    value = "X-User-Roles",
                    required = false)String roles) {
        if (!RoleUtils.isAdmin(roles)) {
            return ApiResponse.fail(
                    403,
                    "无权限"
            );
        }
        if (!personWorkService.removeByCompositeKey(personId, workId, roleType)) {
            return ApiResponse.fail(404, "人物-作品关联不存在");
        }
        return ApiResponse.success();
    }

    @GetMapping("/work/{workId}")
    public ApiResponse<List<WorkPersonVO>> personsByWork(
            @PathVariable Long workId) {

        return ApiResponse.success(
                personWorkService.listPersonsByWork(
                        workId
                )
        );
    }
}
