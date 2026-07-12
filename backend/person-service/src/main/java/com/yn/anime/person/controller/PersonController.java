package com.yn.anime.person.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.yn.anime.common.response.ApiResponse;
import com.yn.anime.person.entity.Person;
import com.yn.anime.person.service.PersonService;
import com.yn.anime.person.service.PersonWorkService;
import com.yn.anime.person.util.RoleUtils;
import com.yn.anime.person.vo.PersonDetailVO;
import com.yn.anime.person.vo.PersonWorkVO;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/persons")
@RequiredArgsConstructor
public class PersonController {

    private final PersonService personService;
    private final PersonWorkService personWorkService;

    @GetMapping("/ping")
    public String ping() {
        return "person-service is running";
    }

    @GetMapping("/{id}")
    public ApiResponse<Person> getById(@PathVariable Long id) {
        Person person = personService.getPersonWithFullUrl(id);
        if (person == null) {
            return ApiResponse.fail(404, "人物不存在");
        }
        return ApiResponse.success(person);
    }

    @GetMapping
    public ApiResponse<Page<Person>> page(
            @RequestParam(defaultValue = "1") long current,
            @RequestParam(defaultValue = "10") long size,
            @RequestParam(required = false) String name,
            @RequestParam(required = false) String country) {
        return ApiResponse.success(personService.pagePersons(current, size, name, country));
    }

    @PostMapping
    public ApiResponse<Person> create(@RequestBody Person person,
                                      @RequestHeader(
                                              value = "X-User-Roles",
                                              required = false)String roles) {
        if (!RoleUtils.isAdmin(roles)) {
            return ApiResponse.fail(
                    403,
                    "无权限"
            );
        }
        personService.save(person);
        return ApiResponse.success(person);
    }

    @PutMapping("/{id}")
    public ApiResponse<Person> update(@PathVariable Long id,
                                      @RequestBody Person person,
                                      @RequestHeader(
                                              value = "X-User-Roles",
                                              required = false)String roles) {
        if (!RoleUtils.isAdmin(roles)) {
            return ApiResponse.fail(
                    403,
                    "无权限"
            );
        }
        person.setId(id);
        if (!personService.updateById(person)) {
            return ApiResponse.fail(404, "人物不存在");
        }
        return ApiResponse.success(personService.getPersonWithFullUrl(id));
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
        if (!personService.removeById(id)) {
            return ApiResponse.fail(404, "人物不存在");
        }
        return ApiResponse.success();
    }

    // 搜索接口
    @GetMapping("/search")
    public ApiResponse<Page<Person>> search(
            @RequestParam String keyword,
            @RequestParam(defaultValue = "1") long current,
            @RequestParam(defaultValue = "10") long size) {

        return ApiResponse.success(
                personService.searchPersons(
                        current,
                        size,
                        keyword
                )
        );
    }

    // 详情查询
    @GetMapping("/{id}/detail")
    public ApiResponse<PersonDetailVO> detail(
            @PathVariable Long id) {

        PersonDetailVO vo =
                personService.getPersonDetail(id);

        if (vo == null) {
            return ApiResponse.fail(
                    404,
                    "人物不存在"
            );
        }

        return ApiResponse.success(vo);
    }

    @GetMapping("/{id}/works")
    public ApiResponse<List<PersonWorkVO>> works(
            @PathVariable Long id) {

        return ApiResponse.success(
                personWorkService.listWorksByPerson(
                        id
                )
        );
    }
}
