package com.yn.anime.notification.client.fallback;

import com.yn.anime.notification.client.AuthUserClient;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class AuthUserClientFallback implements AuthUserClient {
    @Override
    public List<Long> listActiveUserIds() {
        return List.of();
    }
}