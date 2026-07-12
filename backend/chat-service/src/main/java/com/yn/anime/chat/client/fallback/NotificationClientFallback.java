package com.yn.anime.chat.client.fallback;

import com.yn.anime.chat.client.NotificationClient;
import com.yn.anime.common.dto.NotificationCreateRequest;
import com.yn.anime.common.response.ApiResponse;
import org.springframework.stereotype.Component;

@Component
public class NotificationClientFallback implements NotificationClient {
    @Override
    public ApiResponse<String> createNotification(NotificationCreateRequest request) {
        System.err.println("[聊天服务] notification-service 暂时不可用，@人通知降级处理");
        return ApiResponse.fail("notification-service 暂时不可用");
    }
}