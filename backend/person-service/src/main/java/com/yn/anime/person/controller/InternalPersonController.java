package com.yn.anime.person.controller;

import com.yn.anime.common.dto.PersonDetailDTO;
import com.yn.anime.common.dto.PersonSimpleDTO;
import com.yn.anime.person.service.PersonService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/*
    classDescription:
*/
@RestController
@RequestMapping("/internal/persons")
@RequiredArgsConstructor
public class InternalPersonController {
    private final PersonService personService;

    // 查询详情
    @GetMapping("/{id}")
    public PersonDetailDTO getById(
            @PathVariable Long id) {

        var person = personService.getById(id);

        if (person == null) {
            return null;
        }

        PersonDetailDTO dto =
                new PersonDetailDTO();

        BeanUtils.copyProperties(
                person,
                dto
        );

        return dto;
    }

    //查询简要信息
    @GetMapping("/simple/{id}")
    public PersonSimpleDTO simple(
            @PathVariable Long id) {

        var person = personService.getById(id);

        if (person == null) {
            return null;
        }

        PersonSimpleDTO dto =
                new PersonSimpleDTO();

        dto.setId(person.getId());

        dto.setName(person.getName());

        return dto;
    }

    //批量查询
    @PostMapping("/batch")
    public List<PersonSimpleDTO> batch(
            @RequestBody List<Long> ids) {

        return personService.listByIds(ids)
                .stream()
                .map(person -> {

                    PersonSimpleDTO dto =
                            new PersonSimpleDTO();

                    dto.setId(person.getId());

                    dto.setName(person.getName());

                    return dto;
                })
                .toList();
    }

    @GetMapping("/exist/{id}")
    public Boolean exist(
            @PathVariable Long id) {

        return personService.exists(
                id
        );
    }
}
