package com.yn.anime.character.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.yn.anime.character.entity.CharacterRelation;
import com.yn.anime.character.service.CharacterRelationService;
import com.yn.anime.character.util.RoleUtils;
import com.yn.anime.character.vo.CharacterRelationVO;
import com.yn.anime.common.response.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/character-relations")
@RequiredArgsConstructor
public class CharacterRelationController {

    private final CharacterRelationService characterRelationService;

    @GetMapping("/{id}")
    public ApiResponse<CharacterRelation> getById(@PathVariable Long id) {
        CharacterRelation relation = characterRelationService.getById(id);
        if (relation == null) {
            return ApiResponse.fail(404, "角色关系不存在");
        }
        return ApiResponse.success(relation);
    }

    @PostMapping
    public ApiResponse<CharacterRelation> create(
            @RequestBody CharacterRelation relation,
            @RequestHeader(
                    value = "X-User-Roles",
                    required = false)String roles) {
        if (!RoleUtils.isAdmin(roles)) {
            return ApiResponse.fail(
                    403,
                    "无权限"
            );
        }
        characterRelationService.save(relation);
        return ApiResponse.success(relation);
    }

    @PutMapping("/{id}")
    public ApiResponse<CharacterRelation> update(@PathVariable Long id,
                                                 @RequestBody CharacterRelation relation,
                                                 @RequestHeader(
                                                         value = "X-User-Roles",
                                                         required = false)String roles) {
        if (!RoleUtils.isAdmin(roles)) {
            return ApiResponse.fail(
                    403,
                    "无权限"
            );
        }
        relation.setId(id);
        if (!characterRelationService.updateById(relation)) {
            return ApiResponse.fail(404, "角色关系不存在");
        }
        return ApiResponse.success(characterRelationService.getById(id));
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
        if (!characterRelationService.removeById(id)) {
            return ApiResponse.fail(404, "角色关系不存在");
        }
        return ApiResponse.success();
    }

    // 查询某角色的所有关系
    @GetMapping("/character/{characterId}")
    public ApiResponse<List<CharacterRelationVO>> listRelations(
            @PathVariable Long characterId) {

        return ApiResponse.success(
                characterRelationService
                        .listRelations(characterId)
        );
    }
}
