package com.yn.anime.chat.websocket;

import com.yn.anime.chat.dto.ChatMessageDTO;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.AbstractWebSocketHandler;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class ChatWebSocketHandler extends AbstractWebSocketHandler {
    private final ConcurrentHashMap<Long, Set<WebSocketSession>> userSessions = new ConcurrentHashMap<>();

    @Override
    public void afterConnectionEstablished(WebSocketSession session) {
        try {
            Object userIdObj = session.getAttributes().get("userId");

            System.out.println("[聊天WebSocket连接建立] userIdObj=" + userIdObj);

            if (userIdObj == null) {
                session.close(CloseStatus.NOT_ACCEPTABLE.withReason("缺少用户身份"));
                return;
            }

            Long userId = Long.valueOf(userIdObj.toString());

            userSessions.computeIfAbsent(userId, key -> ConcurrentHashMap.newKeySet()).add(session);

            session.sendMessage(new TextMessage("{\"type\":\"CONNECTED\",\"message\":\"聊天 WebSocket 已连接\"}"));

            System.out.println("[聊天WebSocket连接成功] userId=" + userId);
        } catch (Exception e) {
            System.err.println("[聊天WebSocket连接建立异常] " + e.getMessage());
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

            System.out.println("[聊天WebSocket收到消息] " + payload);

            if ("ping".equalsIgnoreCase(payload)) {
                session.sendMessage(new TextMessage("{\"type\":\"PONG\"}"));
            }
        } catch (Exception e) {
            System.err.println("[聊天WebSocket消息处理异常] " + e.getMessage());
            e.printStackTrace();
        }
    }

    @Override
    public void handleTransportError(WebSocketSession session, Throwable exception) {
        System.err.println("[聊天WebSocket传输异常] " + exception.getMessage());
        exception.printStackTrace();
    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) {
        Object userIdObj = session.getAttributes().get("userId");

        System.out.println("[聊天WebSocket连接关闭] userIdObj=" + userIdObj + ", status=" + status);

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

    public void pushChatMessageToUsers(Collection<Long> userIds, ChatMessageDTO message) {
        if (userIds == null || userIds.isEmpty()) {
            return;
        }

        String json = toChatMessagePushJson(message);

        for (Long userId : userIds) {
            pushRawJson(userId, json);
        }
    }

    public void pushMessageDeletedToUsers(Collection<Long> userIds, Long conversationId, Long messageId) {
        if (userIds == null || userIds.isEmpty()) {
            return;
        }

        String json = "{"
                + "\"type\":\"MESSAGE_DELETED\","
                + "\"data\":{"
                + "\"conversationId\":" + number(conversationId) + ","
                + "\"messageId\":" + number(messageId)
                + "}"
                + "}";

        for (Long userId : userIds) {
            pushRawJson(userId, json);
        }
    }

    public void pushConversationStatusToUsers(Collection<Long> userIds,
                                              Long conversationId,
                                              String eventType,
                                              Integer status) {
        if (userIds == null || userIds.isEmpty()) {
            return;
        }

        String json = "{"
                + "\"type\":\"" + escape(eventType) + "\","
                + "\"data\":{"
                + "\"conversationId\":" + number(conversationId) + ","
                + "\"status\":" + number(status)
                + "}"
                + "}";

        for (Long userId : userIds) {
            pushRawJson(userId, json);
        }
    }

    private void pushRawJson(Long userId, String json) {
        Set<WebSocketSession> sessions = userSessions.get(userId);

        if (sessions == null || sessions.isEmpty()) {
            System.out.println("[聊天WebSocket推送] 用户不在线 userId=" + userId);
            return;
        }

        for (WebSocketSession session : sessions) {
            try {
                if (session.isOpen()) {
                    session.sendMessage(new TextMessage(json));
                    System.out.println("[聊天WebSocket推送成功] userId=" + userId);
                }
            } catch (Exception e) {
                System.err.println("[聊天WebSocket推送失败] userId=" + userId + ", error=" + e.getMessage());
                e.printStackTrace();
            }
        }
    }

    private String toChatMessagePushJson(ChatMessageDTO m) {
        return "{"
                + "\"type\":\"CHAT_MESSAGE\","
                + "\"data\":{"
                + "\"id\":" + number(m.id()) + ","
                + "\"conversationId\":" + number(m.conversationId()) + ","
                + "\"senderId\":" + number(m.senderId()) + ","
                + "\"clientMessageId\":\"" + escape(m.clientMessageId()) + "\","
                + "\"messageType\":\"" + escape(m.messageType()) + "\","
                + "\"content\":\"" + escape(m.content()) + "\","
                + "\"mediaUrl\":\"" + escape(m.mediaUrl()) + "\","
                + "\"replyToMessageId\":" + number(m.replyToMessageId()) + ","
                + "\"deleted\":" + bool(m.deleted()) + ","
                + "\"reviewStatus\":\"" + escape(m.reviewStatus()) + "\","
                + "\"mentionUserIds\":" + longList(m.mentionUserIds()) + ","
                + "\"createdAt\":\"" + time(m.createdAt()) + "\""
                + "}"
                + "}";
    }

    private String number(Number value) {
        return value == null ? "null" : value.toString();
    }

    private String bool(Boolean value) {
        return value == null ? "false" : value.toString();
    }

    private String time(LocalDateTime value) {
        return value == null ? "" : value.toString();
    }

    private String longList(Collection<Long> values) {
        if (values == null || values.isEmpty()) {
            return "[]";
        }

        StringBuilder builder = new StringBuilder("[");
        boolean first = true;

        for (Long value : values) {
            if (!first) {
                builder.append(",");
            }
            builder.append(number(value));
            first = false;
        }

        builder.append("]");
        return builder.toString();
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