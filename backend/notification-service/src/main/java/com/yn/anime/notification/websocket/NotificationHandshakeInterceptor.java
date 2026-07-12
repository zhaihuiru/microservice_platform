package com.yn.anime.notification.websocket;

import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.server.HandshakeInterceptor;

import java.net.URI;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.Map;

@Component
public class NotificationHandshakeInterceptor implements HandshakeInterceptor {
    @Override
    public boolean beforeHandshake(ServerHttpRequest request,
                                   ServerHttpResponse response,
                                   WebSocketHandler wsHandler,
                                   Map<String, Object> attributes) {
        String userId = request.getHeaders().getFirst("X-User-Id");

        if (userId == null || userId.isBlank()) {
            userId = getQueryParam(request.getURI(), "userId");
        }

        System.out.println("[WebSocket握手] uri=" + request.getURI() + ", userId=" + userId);

        if (userId == null || userId.isBlank()) {
            System.out.println("[WebSocket握手失败] 缺少 userId");
            return false;
        }

        try {
            Long realUserId = Long.valueOf(userId);
            attributes.put("userId", realUserId);
            return true;
        } catch (NumberFormatException e) {
            System.out.println("[WebSocket握手失败] userId 格式错误：" + userId);
            return false;
        }
    }

    @Override
    public void afterHandshake(ServerHttpRequest request,
                               ServerHttpResponse response,
                               WebSocketHandler wsHandler,
                               Exception exception) {
    }

    private String getQueryParam(URI uri, String name) {
        String query = uri.getRawQuery();

        if (query == null || query.isBlank()) {
            return null;
        }

        String[] pairs = query.split("&");

        for (String pair : pairs) {
            String[] kv = pair.split("=", 2);

            if (kv.length == 2 && kv[0].equals(name)) {
                return URLDecoder.decode(kv[1], StandardCharsets.UTF_8);
            }
        }

        return null;
    }
}