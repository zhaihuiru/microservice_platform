package com.yn.anime.comment.client;

import com.yn.anime.common.dto.NotificationCreateRequest;
import com.yn.anime.comment.client.fallback.NotificationClientFallback;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(name = "notification-service", fallback = NotificationClientFallback.class)
public interface NotificationClient {
    @PostMapping("/api/notifications")
    String createNotification(@RequestBody NotificationCreateRequest request);
}
