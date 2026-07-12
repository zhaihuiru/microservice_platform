package com.yn.anime.chat.websocket;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.socket.config.annotation.EnableWebSocket;
import org.springframework.web.socket.config.annotation.WebSocketConfigurer;
import org.springframework.web.socket.config.annotation.WebSocketHandlerRegistry;

@Configuration
@EnableWebSocket
public class ChatWebSocketConfig implements WebSocketConfigurer {
    private final ChatWebSocketHandler chatWebSocketHandler;
    private final ChatHandshakeInterceptor chatHandshakeInterceptor;

    public ChatWebSocketConfig(ChatWebSocketHandler chatWebSocketHandler,
                               ChatHandshakeInterceptor chatHandshakeInterceptor) {
        this.chatWebSocketHandler = chatWebSocketHandler;
        this.chatHandshakeInterceptor = chatHandshakeInterceptor;
    }

    @Override
    public void registerWebSocketHandlers(WebSocketHandlerRegistry registry) {
        System.out.println("[聊天WebSocket配置] 注册 /ws/chats");

        registry.addHandler(chatWebSocketHandler, "/ws/chats")
                .addInterceptors(chatHandshakeInterceptor)
                .setAllowedOriginPatterns("*");
    }
}