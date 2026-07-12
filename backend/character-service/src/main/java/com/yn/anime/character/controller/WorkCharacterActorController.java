package com.yn.anime.character.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.yn.anime.character.entity.WorkCharacterActor;
import com.yn.anime.character.service.WorkCharacterActorService;
import com.yn.anime.character.util.RoleUtils;
import com.yn.anime.character.vo.WorkCharacterVO;
import com.yn.anime.common.response.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/work-character-actors")
@RequiredArgsConstructor
public class WorkCharacterActorController {

    private final WorkCharacterActorService workCharacterActorService;

    @PostMapping
    public ApiResponse<WorkCharacterActor> create(@RequestBody WorkCharacterActor relation,
                                                  @RequestHeader(
                                                          value = "X-User-Roles",
                                                          required = false)String roles) {
        if (!RoleUtils.isAdmin(roles)) {
            return ApiResponse.fail(
                    403,
                    "无权限"
            );
        }
        workCharacterActorService.save(relation);
        return ApiResponse.success(relation);
    }

    @DeleteMapping
    public ApiResponse<Void> delete(
            @RequestParam Long workId,
            @RequestParam Long characterId,
            @RequestParam Long personId,
            @RequestHeader(
                    value = "X-User-Roles",
                    required = false)String roles) {
        if (!RoleUtils.isAdmin(roles)) {
            return ApiResponse.fail(
                    403,
                    "无权限"
            );
        }
        if (!workCharacterActorService.removeByCompositeKey(workId, characterId, personId)) {
            return ApiResponse.fail(404, "演职关系不存在");
        }
        return ApiResponse.success();
    }

    // 查询作品对应的所有角色
    @GetMapping("/work/{workId}")
    public ApiResponse<List<WorkCharacterVO>> listByWork(
            @PathVariable Long workId){

        return ApiResponse.success(
                workCharacterActorService
                        .listCharactersByWork(workId)
        );
    }
}
