package com.yn.anime.character.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.yn.anime.character.entity.Character;
import com.yn.anime.character.service.CharacterService;
import com.yn.anime.character.util.RoleUtils;
import com.yn.anime.character.vo.CharacterDetailVO;
import com.yn.anime.character.vo.CharacterWorkVO;
import com.yn.anime.character.vo.WorkCharacterVO;
import com.yn.anime.common.response.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/characters")
@RequiredArgsConstructor
public class CharacterController {

    private final CharacterService characterService;

    @GetMapping("/ping")
    public String ping() {
        return "character-service is running";
    }

    @GetMapping("/{id}")
    public ApiResponse<Character> getById(@PathVariable Long id) {
        Character character = characterService.getCharacterWithFullUrl(id);
        if (character == null) {
            return ApiResponse.fail(404, "角色不存在");
        }
        return ApiResponse.success(character);
    }

    @GetMapping
    public ApiResponse<Page<Character>> page(
            @RequestParam(defaultValue = "1") long current,
            @RequestParam(defaultValue = "10") long size,
            @RequestParam(required = false) String name,
            @RequestParam(required = false) String gender) {
        return ApiResponse.success(characterService.pageCharacters(current, size, name, gender));
    }

    @PostMapping
    public ApiResponse<Character> create(@RequestBody Character character,
                                         @RequestHeader(
                                                 value = "X-User-Roles",
                                                 required = false)String roles) {
        if (!RoleUtils.isAdmin(roles)) {
            return ApiResponse.fail(
                    403,
                    "无权限"
            );
        }
        characterService.save(character);
        return ApiResponse.success(character);
    }

    @PutMapping("/{id}")
    public ApiResponse<Character> update(@PathVariable Long id,
                                         @RequestBody Character character,
                                         @RequestHeader(
                                                 value = "X-User-Roles",
                                                 required = false)String roles) {
        if (!RoleUtils.isAdmin(roles)) {
            return ApiResponse.fail(
                    403,
                    "无权限"
            );
        }
        character.setId(id);
        if (!characterService.updateById(character)) {
            return ApiResponse.fail(404, "角色不存在");
        }
        return ApiResponse.success(characterService.getCharacterWithFullUrl(id));
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
        if (!characterService.removeById(id)) {
            return ApiResponse.fail(404, "角色不存在");
        }
        return ApiResponse.success();
    }

    // 搜索接口
    @GetMapping("/search")
    public ApiResponse<Page<Character>> search(
            @RequestParam String keyword,
            @RequestParam(defaultValue = "1") long current,
            @RequestParam(defaultValue = "10") long size) {

        return ApiResponse.success(
                characterService.searchCharacters(
                        current,
                        size,
                        keyword
                )
        );
    }

    // 详情查询
    @GetMapping("/{id}/detail")
    public ApiResponse<CharacterDetailVO> detail(
            @PathVariable Long id) {

        CharacterDetailVO vo =
                characterService.getCharacterDetail(id);

        if (vo == null) {
            return ApiResponse.fail(
                    404,
                    "角色不存在"
            );
        }

        return ApiResponse.success(vo);
    }

    // 查询角色所属作品
    @GetMapping("/{id}/works")
    public ApiResponse<List<CharacterWorkVO>> works(
            @PathVariable Long id){

        return ApiResponse.success(
                characterService.listWorks(id)
        );
    }


}
