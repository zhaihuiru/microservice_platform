package com.yn.anime.work.controller;

import com.yn.anime.common.dto.WorkDetailDTO;
import com.yn.anime.common.dto.WorkSimpleDTO;
import com.yn.anime.common.dto.WorkTitleDTO;
import com.yn.anime.work.entity.Work;
import com.yn.anime.work.service.WorkService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/*
    classDescription:
*/
@RestController
@RequestMapping("/internal/works")
@RequiredArgsConstructor
public class InternalWorkController {
    private final WorkService workService;

    // 批量查询接口
    @PostMapping("/batch")
    public List<WorkSimpleDTO> batch(@RequestBody List<Long> ids) {

        return workService.listByIds(ids)
                .stream()
                .map(work -> {

                    WorkSimpleDTO dto =
                            new WorkSimpleDTO();

                    BeanUtils.copyProperties(
                            work,
                            dto
                    );

                    return dto;
                })
                .toList();
    }

    // 根据id查询作品
    @GetMapping("/{id}")
    public WorkDetailDTO getById(@PathVariable Long id) {

        var work = workService.getById(id);

        if (work == null) {
            return null;
        }

        WorkDetailDTO dto = new WorkDetailDTO();

        BeanUtils.copyProperties(
                work,
                dto
        );

        return dto;
    }

    // 仅获取作品简要信息
    @GetMapping("/simple/{id}")
    public WorkSimpleDTO simple(
            @PathVariable Long id) {

        var work = workService.getById(id);

        if (work == null) {
            return null;
        }

        WorkSimpleDTO dto =
                new WorkSimpleDTO();

        BeanUtils.copyProperties(
                work,
                dto
        );

        return dto;
    }

    // 判断作品是否存在
    @GetMapping("/exist/{id}")
    public Boolean exist(
            @PathVariable Long id) {

        return workService.exists(id);
    }

    @GetMapping("/titles")
    public List<WorkTitleDTO> titles(
            @RequestParam List<Long> ids) {

        return workService.listByIds(ids)
                .stream()
                .map(work -> {

                    WorkTitleDTO dto =
                            new WorkTitleDTO();

                    dto.setId(work.getId());

                    dto.setTitle(work.getTitle());

                    return dto;
                })
                .toList();
    }
}
