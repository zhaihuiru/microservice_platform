package com.yn.anime.notification.websocket;

import com.yn.anime.common.dto.NotificationDTO;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.AbstractWebSocketHandler;

import java.time.LocalDateTime;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class NotificationWebSocketHandler extends AbstractWebSocketHandler {
    private final ConcurrentHashMap<Long, Set<WebSocketSession>> userSessions = new ConcurrentHashMap<>();

    @Override
    public void afterConnectionEstablished(WebSocketSession session) {
        try {
            Object userIdObj = session.getAttributes().get("userId");

            System.out.println("[WebSocket连接建立] userIdObj=" + userIdObj);

            if (userIdObj == null) {
                session.close(CloseStatus.NOT_ACCEPTABLE.withReason("缺少用户身份"));
                return;
            }

            Long userId = Long.valueOf(userIdObj.toString());

            userSessions.computeIfAbsent(userId, key -> ConcurrentHashMap.newKeySet()).add(session);

            session.sendMessage(new TextMessage("{\"type\":\"CONNECTED\",\"message\":\"通知 WebSocket 已连接\"}"));

            System.out.println("[WebSocket连接成功] userId=" + userId);
        } catch (Exception e) {
            System.err.println("[WebSocket连接建立异常] " + e.getMessage());
            e.printStackTrace();

            try {
                session.close(CloseStatus.SERVER_ERROR.withReason("连接建立异常"));
            } catch (Exception ignored) {
            }
        }
    }

    @Override
    public void handleMessage(WebSocketSession session, WebSocketMessage<?> message) {
        try {
            String payload = String.valueOf(message.getPayload());

            System.out.println("[WebSocket收到消息] " + payload);

            if ("ping".equalsIgnoreCase(payload)) {
                session.sendMessage(new TextMessage("{\"type\":\"PONG\"}"));
            }
        } catch (Exception e) {
            System.err.println("[WebSocket消息处理异常] " + e.getMessage());
            e.printStackTrace();
        }
    }

    @Override
    public void handleTransportError(WebSocketSession session, Throwable exception) {
        System.err.println("[WebSocket传输异常] " + exception.getMessage());
        exception.printStackTrace();
    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) {
        Object userIdObj = session.getAttributes().get("userId");

        System.out.println("[WebSocket连接关闭] userIdObj=" + userIdObj + ", status=" + status);

        if (userIdObj == null) {
            return;
        }

        Long userId = Long.valueOf(userIdObj.toString());

        Set<WebSocketSession> sessions = userSessions.get(userId);
        if (sessions != null) {
            sessions.remove(session);

            if (sessions.isEmpty()) {
                userSessions.remove(userId);
            }
        }
    }

    public void pushToUser(Long userId, NotificationDTO notification) {
        Set<WebSocketSession> sessions = userSessions.get(userId);

        if (sessions == null || sessions.isEmpty()) {
            System.out.println("[WebSocket推送] 用户不在线 userId=" + userId);
            return;
        }

        String json = toPushJson(notification);

        for (WebSocketSession session : sessions) {
            try {
                if (session.isOpen()) {
                    session.sendMessage(new TextMessage(json));
                    System.out.println("[WebSocket推送成功] userId=" + userId);
                }
            } catch (Exception e) {
                System.err.println("[WebSocket推送失败] " + e.getMessage());
                e.printStackTrace();
            }
        }
    }

    private String toPushJson(NotificationDTO n) {
        return "{"
                + "\"type\":\"NOTIFICATION\","
                + "\"data\":{"
                + "\"userMessageId\":" + number(n.userMessageId()) + ","
                + "\"messageId\":" + number(n.messageId()) + ","
                + "\"receiverId\":" + number(n.receiverId()) + ","
                + "\"senderId\":" + number(n.senderId()) + ","
                + "\"title\":\"" + escape(n.title()) + "\","
                + "\"content\":\"" + escape(n.content()) + "\","
                + "\"noticeType\":\"" + escape(n.noticeType()) + "\","
                + "\"targetType\":\"" + escape(n.targetType()) + "\","
                + "\"targetId\":" + number(n.targetId()) + ","
                + "\"readStatus\":" + number(n.readStatus()) + ","
                + "\"readAt\":\"" + time(n.readAt()) + "\","
                + "\"createdAt\":\"" + time(n.createdAt()) + "\""
                + "}"
                + "}";
    }

    private String number(Number value) {
        return value == null ? "null" : value.toString();
    }

    private String time(LocalDateTime value) {
        return value == null ? "" : value.toString();
    }

    private String escape(String value) {
        if (value == null) {
            return "";
        }

        return value
                .replace("\\", "\\\\")
                .replace("\"", "\\\"")
                .replace("\n", "\\n")
                .replace("\r", "\\r");
    }
}