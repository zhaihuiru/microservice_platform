package com.yn.anime.notification.client;

import com.yn.anime.notification.client.fallback.AuthUserClientFallback;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;

@FeignClient(
        name = "auth-service",
        path = "/api/users/internal",
        fallback = AuthUserClientFallback.class
)
public interface AuthUserClient {
    @GetMapping("/active-user-ids")
    List<Long> listActiveUserIds();
}