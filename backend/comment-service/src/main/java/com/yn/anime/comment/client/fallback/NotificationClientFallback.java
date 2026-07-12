package com.yn.anime.comment.client.fallback;

import com.yn.anime.comment.client.NotificationClient;
import com.yn.anime.common.dto.NotificationCreateRequest;
import org.springframework.stereotype.Component;

@Component
public class NotificationClientFallback implements NotificationClient {
    @Override
    public String createNotification(NotificationCreateRequest request) {
        return "notification-service unavailable, fallback logged";
    }
}
