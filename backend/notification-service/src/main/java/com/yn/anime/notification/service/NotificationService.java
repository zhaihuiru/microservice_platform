package com.yn.anime.notification.service;

import com.yn.anime.common.dto.NotificationCreateRequest;
import com.yn.anime.common.dto.NotificationDTO;
import com.yn.anime.common.dto.BroadcastNotificationRequest;
import com.yn.anime.common.dto.BroadcastResultDTO;
import com.yn.anime.notification.client.AuthUserClient;
import com.yn.anime.notification.entity.NoticeMessage;
import com.yn.anime.notification.entity.NoticeUserMessage;
import com.yn.anime.notification.repository.NoticeMessageRepository;
import com.yn.anime.notification.repository.NoticeUserMessageRepository;
import com.yn.anime.notification.websocket.NotificationWebSocketHandler;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class NotificationService {
    private final NoticeMessageRepository messageRepository;
    private final NoticeUserMessageRepository userMessageRepository;
    private final AuthUserClient authUserClient;
    private final NotificationWebSocketHandler webSocketHandler;

    public NotificationService(NoticeMessageRepository messageRepository,
                               NoticeUserMessageRepository userMessageRepository,
                               AuthUserClient authUserClient,
                               NotificationWebSocketHandler webSocketHandler) {
        this.messageRepository = messageRepository;
        this.userMessageRepository = userMessageRepository;
        this.authUserClient = authUserClient;
        this.webSocketHandler = webSocketHandler;
    }

    @Transactional
    public NotificationDTO sendToUser(NotificationCreateRequest request) {
        validateCreateRequest(request);

        NoticeMessage message = new NoticeMessage();
        message.setTitle(request.title().trim());
        message.setContent(request.content() == null ? "" : request.content().trim());
        message.setNoticeType(isBlank(request.noticeType()) ? "SYSTEM" : request.noticeType().trim());
        message.setSenderId(request.senderId());
        message.setTargetType(isBlank(request.targetType()) ? "SYSTEM" : request.targetType().trim());
        message.setTargetId(request.targetId());

        NoticeMessage savedMessage = messageRepository.save(message);

        NoticeUserMessage userMessage = new NoticeUserMessage();
        userMessage.setMessageId(savedMessage.getId());
        userMessage.setReceiverId(request.receiverId());
        userMessage.setReadStatus(0);
        userMessage.setDeleted(false);

        NoticeUserMessage savedUserMessage = userMessageRepository.save(userMessage);

        NotificationDTO dto = toDTO(savedUserMessage, savedMessage);
        webSocketHandler.pushToUser(dto.receiverId(), dto);

        return dto;
    }

    @Transactional
    public NotificationDTO adminSendToUser(Long adminId, Long receiverId, NotificationCreateRequest request) {
        NotificationCreateRequest realRequest = new NotificationCreateRequest(
                receiverId,
                adminId,
                request.title(),
                request.content(),
                isBlank(request.noticeType()) ? "SYSTEM" : request.noticeType(),
                isBlank(request.targetType()) ? "SYSTEM" : request.targetType(),
                request.targetId()
        );

        return sendToUser(realRequest);
    }

    public Page<NotificationDTO> listMyNotifications(Long userId, int page, int size) {
        return userMessageRepository
                .findByReceiverIdAndDeletedFalseOrderByCreatedAtDesc(userId, PageRequest.of(page, size))
                .map(this::toDTO);
    }

    public long countUnread(Long userId) {
        return userMessageRepository.countByReceiverIdAndReadStatusAndDeletedFalse(userId, 0);
    }

    @Transactional
    public NotificationDTO markRead(Long userId, Long userMessageId) {
        NoticeUserMessage userMessage = userMessageRepository
                .findByIdAndReceiverIdAndDeletedFalse(userMessageId, userId)
                .orElseThrow(() -> new IllegalArgumentException("通知不存在或无权操作"));

        if (userMessage.getReadStatus() == null || userMessage.getReadStatus() == 0) {
            userMessage.setReadStatus(1);
            userMessage.setReadAt(LocalDateTime.now());
        }

        NoticeUserMessage saved = userMessageRepository.save(userMessage);
        return toDTO(saved);
    }

    @Transactional
    public int readAll(Long userId) {
        List<NoticeUserMessage> unreadList =
                userMessageRepository.findByReceiverIdAndReadStatusAndDeletedFalse(userId, 0);

        LocalDateTime now = LocalDateTime.now();

        for (NoticeUserMessage item : unreadList) {
            item.setReadStatus(1);
            item.setReadAt(now);
        }

        userMessageRepository.saveAll(unreadList);
        return unreadList.size();
    }

    @Transactional
    public void deleteMyNotification(Long userId, Long userMessageId) {
        NoticeUserMessage userMessage = userMessageRepository
                .findByIdAndReceiverIdAndDeletedFalse(userMessageId, userId)
                .orElseThrow(() -> new IllegalArgumentException("通知不存在或无权操作"));

        userMessage.setDeleted(true);
        userMessageRepository.save(userMessage);
    }

    private NotificationDTO toDTO(NoticeUserMessage userMessage) {
        NoticeMessage message = messageRepository.findById(userMessage.getMessageId())
                .orElse(null);

        return toDTO(userMessage, message);
    }

    private NotificationDTO toDTO(NoticeUserMessage userMessage, NoticeMessage message) {
        if (message == null) {
            return new NotificationDTO(
                    userMessage.getId(),
                    userMessage.getMessageId(),
                    userMessage.getReceiverId(),
                    null,
                    "通知内容不存在",
                    "",
                    "UNKNOWN",
                    "UNKNOWN",
                    null,
                    userMessage.getReadStatus(),
                    userMessage.getReadAt(),
                    userMessage.getCreatedAt()
            );
        }

        return new NotificationDTO(
                userMessage.getId(),
                message.getId(),
                userMessage.getReceiverId(),
                message.getSenderId(),
                message.getTitle(),
                message.getContent(),
                message.getNoticeType(),
                message.getTargetType(),
                message.getTargetId(),
                userMessage.getReadStatus(),
                userMessage.getReadAt(),
                userMessage.getCreatedAt()
        );
    }

    private void validateCreateRequest(NotificationCreateRequest request) {
        if (request == null) {
            throw new IllegalArgumentException("通知请求不能为空");
        }

        if (request.receiverId() == null) {
            throw new IllegalArgumentException("接收人 ID 不能为空");
        }

        if (isBlank(request.title())) {
            throw new IllegalArgumentException("通知标题不能为空");
        }

        if (request.title().length() > 100) {
            throw new IllegalArgumentException("通知标题不能超过 100 个字符");
        }
    }

    private boolean isBlank(String value) {
        return value == null || value.trim().isEmpty();
    }

    @Transactional
    public BroadcastResultDTO broadcastToAll(Long adminId, BroadcastNotificationRequest request) {
        validateBroadcastRequest(request);

        List<Long> receiverIds = authUserClient.listActiveUserIds();

        if (receiverIds == null || receiverIds.isEmpty()) {
            throw new IllegalStateException("没有可接收公告的用户，或 auth-service 暂时不可用");
        }

        NoticeMessage message = new NoticeMessage();
        message.setTitle(request.title().trim());
        message.setContent(request.content() == null ? "" : request.content().trim());
        message.setNoticeType(isBlank(request.noticeType()) ? "SYSTEM" : request.noticeType().trim());
        message.setSenderId(adminId);
        message.setTargetType(isBlank(request.targetType()) ? "SYSTEM" : request.targetType().trim());
        message.setTargetId(request.targetId());
        message.setDeleted(false);

        NoticeMessage savedMessage = messageRepository.save(message);

        List<NoticeUserMessage> userMessages = receiverIds.stream().map(receiverId -> {
            NoticeUserMessage item = new NoticeUserMessage();
            item.setMessageId(savedMessage.getId());
            item.setReceiverId(receiverId);
            item.setReadStatus(0);
            item.setDeleted(false);
            return item;
        }).toList();

        List<NoticeUserMessage> savedUserMessages = userMessageRepository.saveAll(userMessages);

        for (NoticeUserMessage userMessage : savedUserMessages) {
            NotificationDTO dto = toDTO(userMessage, savedMessage);
            webSocketHandler.pushToUser(dto.receiverId(), dto);
        }

        return new BroadcastResultDTO(
                savedMessage.getId(),
                savedMessage.getTitle(),
                savedUserMessages.size()
        );
    }

    @Transactional
    public void adminDeleteMessage(Long adminId, Long messageId) {
        NoticeMessage message = messageRepository.findById(messageId)
                .orElseThrow(() -> new IllegalArgumentException("通知内容不存在"));

        message.setDeleted(true);
        messageRepository.save(message);

        List<NoticeUserMessage> userMessages = userMessageRepository.findByMessageIdAndDeletedFalse(messageId);
        for (NoticeUserMessage item : userMessages) {
            item.setDeleted(true);
        }
        userMessageRepository.saveAll(userMessages);
    }

    @Transactional
    public void adminDeleteUserMessage(Long adminId, Long userMessageId) {
        NoticeUserMessage userMessage = userMessageRepository.findByIdAndDeletedFalse(userMessageId)
                .orElseThrow(() -> new IllegalArgumentException("用户通知不存在"));

        userMessage.setDeleted(true);
        userMessageRepository.save(userMessage);
    }

    private void validateBroadcastRequest(BroadcastNotificationRequest request) {
        if (request == null) {
            throw new IllegalArgumentException("公告请求不能为空");
        }

        if (isBlank(request.title())) {
            throw new IllegalArgumentException("公告标题不能为空");
        }

        if (request.title().length() > 100) {
            throw new IllegalArgumentException("公告标题不能超过 100 个字符");
        }
    }
}