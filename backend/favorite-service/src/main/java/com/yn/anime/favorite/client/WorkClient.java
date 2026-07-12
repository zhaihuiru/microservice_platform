package com.yn.anime.favorite.client;

import com.yn.anime.common.dto.WorkDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

/**
 * work-service Feign 客户端（验证作品是否存在）
 */
@FeignClient(name = "work-service")
public interface WorkClient {

    /**
     * 根据ID获取作品信息（调用 work-service 内部接口，直返DTO无ApiResponse包装）
     */
    @GetMapping("/internal/works/{id}")
    WorkDTO getWorkById(@PathVariable("id") Long id);
}
