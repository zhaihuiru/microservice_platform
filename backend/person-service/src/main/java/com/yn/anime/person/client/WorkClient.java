package com.yn.anime.person.client;

import com.yn.anime.common.dto.WorkSimpleDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;

@FeignClient(name = "work-service")
public interface WorkClient {

    @GetMapping("/internal/works/simple/{id}")
    WorkSimpleDTO getSimple(
            @PathVariable("id") Long id
    );

    @PostMapping("/internal/works/batch")
    List<WorkSimpleDTO> batch(
            @RequestBody List<Long> ids
    );

    @GetMapping("/internal/works/exist/{id}")
    Boolean exist(
            @PathVariable("id") Long id
    );
}