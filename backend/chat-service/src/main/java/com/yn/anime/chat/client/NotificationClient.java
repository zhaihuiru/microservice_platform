package com.yn.anime.chat.client;

import com.yn.anime.chat.client.fallback.NotificationClientFallback;
import com.yn.anime.common.dto.NotificationCreateRequest;
import com.yn.anime.common.response.ApiResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(
        name = "notification-service",
        fallback = NotificationClientFallback.class
)
public interface NotificationClient {
    @PostMapping("/api/notifications")
    ApiResponse<String> createNotification(@RequestBody NotificationCreateRequest request);
}