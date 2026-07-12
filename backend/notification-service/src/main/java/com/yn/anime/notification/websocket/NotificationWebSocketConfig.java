package com.yn.anime.notification.websocket;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.socket.config.annotation.EnableWebSocket;
import org.springframework.web.socket.config.annotation.WebSocketConfigurer;
import org.springframework.web.socket.config.annotation.WebSocketHandlerRegistry;

@Configuration
@EnableWebSocket
public class NotificationWebSocketConfig implements WebSocketConfigurer {
    private final NotificationWebSocketHandler notificationWebSocketHandler;
    private final NotificationHandshakeInterceptor notificationHandshakeInterceptor;

    public NotificationWebSocketConfig(NotificationWebSocketHandler notificationWebSocketHandler,
                                       NotificationHandshakeInterceptor notificationHandshakeInterceptor) {
        this.notificationWebSocketHandler = notificationWebSocketHandler;
        this.notificationHandshakeInterceptor = notificationHandshakeInterceptor;
    }

    @Override
    public void registerWebSocketHandlers(WebSocketHandlerRegistry registry) {
        System.out.println("[WebSocket配置] 注册 /ws/notifications");

        registry.addHandler(notificationWebSocketHandler, "/ws/notifications")
                .addInterceptors(notificationHandshakeInterceptor)
                .setAllowedOriginPatterns("*");
    }
}