package com.yn.anime.chat.service;

import com.yn.anime.chat.dto.*;
import com.yn.anime.chat.entity.ChatConversation;
import com.yn.anime.chat.entity.ChatConversationMember;
import com.yn.anime.chat.entity.ChatMessage;
import com.yn.anime.chat.entity.ChatMessageMention;
import com.yn.anime.chat.entity.ChatModerationLog;
import com.yn.anime.chat.repository.ChatModerationLogRepository;
import com.yn.anime.chat.repository.ChatConversationMemberRepository;
import com.yn.anime.chat.repository.ChatConversationRepository;
import com.yn.anime.chat.repository.ChatMessageMentionRepository;
import com.yn.anime.chat.repository.ChatMessageRepository;
import com.yn.anime.chat.client.NotificationClient;
import com.yn.anime.chat.websocket.ChatWebSocketHandler;
import com.yn.anime.common.dto.NotificationCreateRequest;
import com.yn.anime.common.response.ApiResponse;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

@Service
public class ChatService {
    private final ChatConversationRepository conversationRepository;
    private final ChatConversationMemberRepository memberRepository;
    private final ChatMessageRepository messageRepository;
    private final ChatMessageMentionRepository mentionRepository;
    private final ChatModerationLogRepository moderationLogRepository;
    private final NotificationClient notificationClient;
    private final ChatWebSocketHandler chatWebSocketHandler;

    public ChatService(ChatConversationRepository conversationRepository,
                       ChatConversationMemberRepository memberRepository,
                       ChatMessageRepository messageRepository,
                       ChatMessageMentionRepository mentionRepository,
                       ChatModerationLogRepository moderationLogRepository,
                       NotificationClient notificationClient,
                       ChatWebSocketHandler chatWebSocketHandler) {
        this.conversationRepository = conversationRepository;
        this.memberRepository = memberRepository;
        this.messageRepository = messageRepository;
        this.mentionRepository = mentionRepository;
        this.moderationLogRepository = moderationLogRepository;
        this.notificationClient = notificationClient;
        this.chatWebSocketHandler = chatWebSocketHandler;
    }

    @Transactional
    public ChatConversationDTO createPrivateConversation(Long currentUserId,
                                                         CreatePrivateConversationRequest request) {
        requireUserId(currentUserId);

        if (request == null || request.peerUserId() == null) {
            throw new IllegalArgumentException("私聊对象不能为空");
        }

        Long peerUserId = request.peerUserId();

        if (Objects.equals(currentUserId, peerUserId)) {
            throw new IllegalArgumentException("不能和自己创建私聊");
        }

        List<ChatConversation> existing =
                conversationRepository.findPrivateConversationBetween(currentUserId, peerUserId);

        if (!existing.isEmpty()) {
            return toConversationDTO(existing.get(0), currentUserId);
        }

        ChatConversation conversation = new ChatConversation();
        conversation.setConversationType("PRIVATE");
        conversation.setTitle("私聊");
        conversation.setOwnerId(currentUserId);
        conversation.setStatus(0);

        ChatConversation savedConversation = conversationRepository.save(conversation);

        addMemberInternal(savedConversation.getId(), currentUserId, "MEMBER");
        addMemberInternal(savedConversation.getId(), peerUserId, "MEMBER");

        return toConversationDTO(savedConversation, currentUserId);
    }

    @Transactional
    public ChatConversationDTO createGroupConversation(Long currentUserId,
                                                       CreateGroupConversationRequest request) {
        requireUserId(currentUserId);

        if (request == null || request.title() == null || request.title().isBlank()) {
            throw new IllegalArgumentException("群聊名称不能为空");
        }

        ChatConversation conversation = new ChatConversation();
        conversation.setConversationType("GROUP");
        conversation.setTitle(request.title().trim());
        conversation.setOwnerId(currentUserId);
        conversation.setStatus(0);

        ChatConversation savedConversation = conversationRepository.save(conversation);

        addMemberInternal(savedConversation.getId(), currentUserId, "OWNER");

        Set<Long> memberIds = new LinkedHashSet<>();

        if (request.memberIds() != null) {
            memberIds.addAll(request.memberIds());
        }

        memberIds.remove(currentUserId);

        for (Long memberId : memberIds) {
            if (memberId != null) {
                addMemberInternal(savedConversation.getId(), memberId, "MEMBER");
            }
        }

        return toConversationDTO(savedConversation, currentUserId);
    }

    @Transactional
    public ChatConversationDTO createWorkConversation(Long currentUserId,
                                                      CreateWorkConversationRequest request) {
        requireUserId(currentUserId);

        if (request == null || request.workId() == null) {
            throw new IllegalArgumentException("作品 ID 不能为空");
        }

        if (request.title() == null || request.title().isBlank()) {
            throw new IllegalArgumentException("作品讨论区名称不能为空");
        }

        Optional<ChatConversation> existing =
                conversationRepository.findByConversationTypeAndWorkIdAndStatus(
                        "WORK_DISCUSSION",
                        request.workId(),
                        0
                );

        if (existing.isPresent()) {
            ChatConversation conversation = existing.get();

            if (!memberRepository.existsByConversationIdAndUserIdAndDeletedFalse(conversation.getId(), currentUserId)) {
                addMemberInternal(conversation.getId(), currentUserId, "MEMBER");
            }

            return toConversationDTO(conversation, currentUserId);
        }

        ChatConversation conversation = new ChatConversation();
        conversation.setConversationType("WORK_DISCUSSION");
        conversation.setTitle(request.title().trim());
        conversation.setWorkId(request.workId());
        conversation.setOwnerId(currentUserId);
        conversation.setStatus(0);

        ChatConversation savedConversation = conversationRepository.save(conversation);

        addMemberInternal(savedConversation.getId(), currentUserId, "OWNER");

        return toConversationDTO(savedConversation, currentUserId);
    }

    @Transactional
    public ChatConversationDTO addMembers(Long currentUserId,
                                          Long conversationId,
                                          AddMemberRequest request) {
        requireUserId(currentUserId);

        ChatConversation conversation = getConversationOrThrow(conversationId);
        ensureConversationActive(conversation);
        ChatConversationMember currentMember = getActiveMemberOrThrow(conversationId, currentUserId);

        if (!isManager(currentMember)) {
            throw new IllegalArgumentException("只有群主或群管理员可以拉人进群");
        }

        if (request == null || request.userIds() == null || request.userIds().isEmpty()) {
            throw new IllegalArgumentException("待添加用户不能为空");
        }

        for (Long userId : new LinkedHashSet<>(request.userIds())) {
            if (userId != null) {
                addMemberInternal(conversationId, userId, "MEMBER");
            }
        }

        return toConversationDTO(conversation, currentUserId);
    }

    @Transactional
    public ChatConversationDTO joinConversation(Long currentUserId, Long conversationId) {
        requireUserId(currentUserId);

        ChatConversation conversation = getConversationOrThrow(conversationId);
        ensureConversationActive(conversation);

        if ("PRIVATE".equals(conversation.getConversationType())) {
            throw new IllegalArgumentException("不能主动加入私聊会话");
        }

        addMemberInternal(conversationId, currentUserId, "MEMBER");

        return toConversationDTO(conversation, currentUserId);
    }

    @Transactional
    public String leaveConversation(Long currentUserId, Long conversationId) {
        requireUserId(currentUserId);

        ChatConversation conversation = getConversationOrThrow(conversationId);
        ensureConversationActive(conversation);

        ChatConversationMember member = getActiveMemberOrThrow(conversationId, currentUserId);

        if ("PRIVATE".equals(conversation.getConversationType())) {
            throw new IllegalArgumentException("私聊不能退出");
        }

        if ("OWNER".equals(member.getMemberRole())) {
            long memberCount = memberRepository.countByConversationIdAndDeletedFalse(conversationId);
            if (memberCount > 1) {
                throw new IllegalArgumentException("群主不能直接退群，请先转让群主或由管理员关闭群聊");
            }
        }

        member.setDeleted(true);
        memberRepository.save(member);

        return "退群成功";
    }

    @Transactional(readOnly = true)
    public List<ChatConversationDTO> listMyConversations(Long currentUserId) {
        requireUserId(currentUserId);

        List<ChatConversationMember> members =
                memberRepository.findByUserIdAndDeletedFalseOrderByUpdatedAtDesc(currentUserId);

        List<ChatConversationDTO> result = new ArrayList<>();

        for (ChatConversationMember member : members) {
            conversationRepository.findById(member.getConversationId())
                    .ifPresent(conversation -> result.add(toConversationDTO(conversation, currentUserId)));
        }

        return result;
    }

    @Transactional(readOnly = true)
    public List<ChatMessageDTO> listMessages(Long currentUserId,
                                             Long conversationId,
                                             int page,
                                             int size) {
        requireUserId(currentUserId);

        getActiveMemberOrThrow(conversationId, currentUserId);

        int realPage = Math.max(page, 0);
        int realSize = Math.min(Math.max(size, 1), 100);

        return messageRepository
                .findByConversationIdAndDeletedFalseOrderByCreatedAtAsc(
                        conversationId,
                        PageRequest.of(realPage, realSize)
                )
                .getContent()
                .stream()
                .map(this::toMessageDTO)
                .toList();
    }

    @Transactional
    public ChatMessageDTO sendMessage(Long currentUserId,
                                      Long conversationId,
                                      SendChatMessageRequest request) {
        requireUserId(currentUserId);

        ChatConversation conversation = getConversationOrThrow(conversationId);
        ensureConversationActive(conversation);

        ChatConversationMember member = getActiveMemberOrThrow(conversationId, currentUserId);

        if (Boolean.TRUE.equals(member.getMuted())) {
            throw new IllegalArgumentException("你已被禁言，不能发送消息");
        }

        validateMessageRequest(request);

        Optional<ChatMessage> existing =
                messageRepository.findByConversationIdAndSenderIdAndClientMessageId(
                        conversationId,
                        currentUserId,
                        request.clientMessageId()
                );

        if (existing.isPresent()) {
            return toMessageDTO(existing.get());
        }

        ChatMessage message = new ChatMessage();
        message.setConversationId(conversationId);
        message.setSenderId(currentUserId);
        message.setClientMessageId(request.clientMessageId().trim());
        message.setMessageType(request.messageType().trim().toUpperCase());
        message.setContent(request.content());
        message.setMediaUrl(request.mediaUrl());
        message.setReplyToMessageId(request.replyToMessageId());
        message.setDeleted(false);
        message.setReviewStatus("PASS");

        ChatMessage savedMessage = messageRepository.save(message);

        List<Long> validMentionUserIds = saveMentionsAndReturnValidUserIds(
                savedMessage.getId(),
                conversationId,
                currentUserId,
                request.mentionUserIds()
        );

        ChatMessageDTO dto = toMessageDTO(savedMessage);

        // 1. 聊天 WebSocket 推送给所有在线会话成员
        pushChatMessageToMembers(conversationId, dto);

        // 2. @人联动 notification-service
        sendMentionNotifications(currentUserId, conversation, savedMessage, validMentionUserIds);

        return dto;
    }

    @Transactional
    public String markConversationRead(Long currentUserId, Long conversationId) {
        requireUserId(currentUserId);

        ChatConversationMember member = getActiveMemberOrThrow(conversationId, currentUserId);

        Optional<ChatMessage> latestMessage =
                messageRepository.findTopByConversationIdAndDeletedFalseOrderByIdDesc(conversationId);

        latestMessage.ifPresent(message -> member.setLastReadMessageId(message.getId()));

        memberRepository.save(member);

        return "已标记为已读";
    }

    @Transactional
    public String deleteMyMessage(Long currentUserId, Long messageId) {
        requireUserId(currentUserId);

        ChatMessage message = messageRepository.findById(messageId)
                .orElseThrow(() -> new IllegalArgumentException("消息不存在"));

        if (!Objects.equals(message.getSenderId(), currentUserId)) {
            throw new IllegalArgumentException("只能删除自己发送的消息");
        }

        getActiveMemberOrThrow(message.getConversationId(), currentUserId);

        message.setDeleted(true);
        messageRepository.save(message);

        pushMessageDeletedToMembers(message.getConversationId(), messageId);

        return "消息已删除";
    }

    @Transactional(readOnly = true)
    public List<ChatConversationDTO> adminListConversations(Long adminId, int page, int size) {
        requireUserId(adminId);

        int realPage = Math.max(page, 0);
        int realSize = Math.min(Math.max(size, 1), 100);

        return conversationRepository
                .findAllByOrderByCreatedAtDesc(PageRequest.of(realPage, realSize))
                .getContent()
                .stream()
                .map(conversation -> toConversationDTO(conversation, adminId))
                .toList();
    }

    @Transactional(readOnly = true)
    public List<ChatMessageDTO> adminListMessages(Long adminId,
                                                  Long conversationId,
                                                  int page,
                                                  int size) {
        requireUserId(adminId);

        getConversationOrThrow(conversationId);

        int realPage = Math.max(page, 0);
        int realSize = Math.min(Math.max(size, 1), 100);

        return messageRepository
                .findByConversationIdOrderByCreatedAtDesc(
                        conversationId,
                        PageRequest.of(realPage, realSize)
                )
                .getContent()
                .stream()
                .map(this::toMessageDTO)
                .toList();
    }

    @Transactional
    public String adminDeleteMessage(Long adminId,
                                     Long messageId,
                                     AdminActionRequest request) {
        requireUserId(adminId);

        ChatMessage message = messageRepository.findById(messageId)
                .orElseThrow(() -> new IllegalArgumentException("消息不存在"));

        if (Boolean.TRUE.equals(message.getDeleted())) {
            return "消息已是删除状态";
        }

        message.setDeleted(true);
        messageRepository.save(message);

        saveModerationLog(
                adminId,
                message.getConversationId(),
                null,
                messageId,
                "DELETE_MESSAGE",
                getReason(request, "管理员删除违规消息")
        );

        pushMessageDeletedToMembers(message.getConversationId(), messageId);

        return "消息已删除";
    }

    @Transactional
    public String adminMuteUser(Long adminId,
                                Long conversationId,
                                Long targetUserId,
                                AdminActionRequest request) {
        requireUserId(adminId);

        getConversationOrThrow(conversationId);

        ChatConversationMember member = getActiveMemberOrThrow(conversationId, targetUserId);

        if (Boolean.TRUE.equals(member.getMuted())) {
            return "用户已处于禁言状态";
        }

        member.setMuted(true);
        memberRepository.save(member);

        saveModerationLog(
                adminId,
                conversationId,
                targetUserId,
                null,
                "MUTE_USER",
                getReason(request, "管理员禁言用户")
        );

        return "禁言成功";
    }

    @Transactional
    public String adminUnmuteUser(Long adminId,
                                  Long conversationId,
                                  Long targetUserId,
                                  AdminActionRequest request) {
        requireUserId(adminId);

        getConversationOrThrow(conversationId);

        ChatConversationMember member = getActiveMemberOrThrow(conversationId, targetUserId);

        if (!Boolean.TRUE.equals(member.getMuted())) {
            return "用户当前未被禁言";
        }

        member.setMuted(false);
        memberRepository.save(member);

        saveModerationLog(
                adminId,
                conversationId,
                targetUserId,
                null,
                "UNMUTE_USER",
                getReason(request, "管理员解除禁言")
        );

        return "解除禁言成功";
    }

    @Transactional
    public String adminCloseConversation(Long adminId,
                                         Long conversationId,
                                         AdminActionRequest request) {
        requireUserId(adminId);

        ChatConversation conversation = getConversationOrThrow(conversationId);

        if (conversation.getStatus() != null && conversation.getStatus() == 1) {
            return "会话已是关闭状态";
        }

        conversation.setStatus(1);
        conversationRepository.save(conversation);

        saveModerationLog(
                adminId,
                conversationId,
                null,
                null,
                "CLOSE_CONVERSATION",
                getReason(request, "管理员关闭会话")
        );

        List<Long> memberIds = memberRepository.findActiveUserIdsByConversationId(conversationId);
        chatWebSocketHandler.pushConversationStatusToUsers(
                memberIds,
                conversationId,
                "CONVERSATION_CLOSED",
                1
        );

        return "会话已关闭";
    }

    @Transactional
    public String adminDisbandConversation(Long adminId,
                                           Long conversationId,
                                           AdminActionRequest request) {
        requireUserId(adminId);

        ChatConversation conversation = getConversationOrThrow(conversationId);

        if ("PRIVATE".equals(conversation.getConversationType())) {
            throw new IllegalArgumentException("私聊会话不能解散");
        }

        if (conversation.getStatus() != null && conversation.getStatus() == 2) {
            return "会话已是解散状态";
        }

        conversation.setStatus(2);
        conversationRepository.save(conversation);

        saveModerationLog(
                adminId,
                conversationId,
                null,
                null,
                "DISBAND_GROUP",
                getReason(request, "管理员解散群聊")
        );

        List<Long> memberIds = memberRepository.findActiveUserIdsByConversationId(conversationId);
        chatWebSocketHandler.pushConversationStatusToUsers(
                memberIds,
                conversationId,
                "CONVERSATION_DISBANDED",
                2
        );

        return "群聊已解散";
    }

    @Transactional(readOnly = true)
    public List<ChatModerationLogDTO> adminListLogs(Long adminId,
                                                    int page,
                                                    int size) {
        requireUserId(adminId);

        int realPage = Math.max(page, 0);
        int realSize = Math.min(Math.max(size, 1), 100);

        return moderationLogRepository
                .findAllByOrderByCreatedAtDesc(PageRequest.of(realPage, realSize))
                .getContent()
                .stream()
                .map(this::toModerationLogDTO)
                .toList();
    }

    private void addMemberInternal(Long conversationId, Long userId, String role) {
        Optional<ChatConversationMember> existing =
                memberRepository.findByConversationIdAndUserId(conversationId, userId);

        if (existing.isPresent()) {
            ChatConversationMember member = existing.get();
            member.setDeleted(false);

            if (member.getMemberRole() == null || member.getMemberRole().isBlank()) {
                member.setMemberRole(role);
            }

            memberRepository.save(member);
            return;
        }

        ChatConversationMember member = new ChatConversationMember();
        member.setConversationId(conversationId);
        member.setUserId(userId);
        member.setMemberRole(role);
        member.setMuted(false);
        member.setDeleted(false);

        memberRepository.save(member);
    }

    private ChatConversation getConversationOrThrow(Long conversationId) {
        if (conversationId == null) {
            throw new IllegalArgumentException("会话 ID 不能为空");
        }

        return conversationRepository.findById(conversationId)
                .orElseThrow(() -> new IllegalArgumentException("会话不存在"));
    }

    private ChatConversationMember getActiveMemberOrThrow(Long conversationId, Long userId) {
        return memberRepository.findByConversationIdAndUserIdAndDeletedFalse(conversationId, userId)
                .orElseThrow(() -> new IllegalArgumentException("你不是该会话成员"));
    }

    private void ensureConversationActive(ChatConversation conversation) {
        if (conversation.getStatus() == null || conversation.getStatus() != 0) {
            throw new IllegalArgumentException("会话已关闭，不能继续操作");
        }
    }

    private boolean isManager(ChatConversationMember member) {
        return "OWNER".equals(member.getMemberRole()) || "ADMIN".equals(member.getMemberRole());
    }

    private void validateMessageRequest(SendChatMessageRequest request) {
        if (request == null) {
            throw new IllegalArgumentException("消息内容不能为空");
        }

        if (request.clientMessageId() == null || request.clientMessageId().isBlank()) {
            throw new IllegalArgumentException("clientMessageId 不能为空");
        }

        if (request.messageType() == null || request.messageType().isBlank()) {
            throw new IllegalArgumentException("消息类型不能为空");
        }

        String messageType = request.messageType().trim().toUpperCase();

        if (!List.of("TEXT", "EMOJI", "IMAGE").contains(messageType)) {
            throw new IllegalArgumentException("消息类型只支持 TEXT / EMOJI / IMAGE");
        }

        if ("TEXT".equals(messageType) || "EMOJI".equals(messageType)) {
            if (request.content() == null || request.content().isBlank()) {
                throw new IllegalArgumentException("文字或表情消息内容不能为空");
            }
        }

        if ("IMAGE".equals(messageType)) {
            if (request.mediaUrl() == null || request.mediaUrl().isBlank()) {
                throw new IllegalArgumentException("图片消息必须包含 mediaUrl");
            }
        }
    }

    private void requireUserId(Long userId) {
        if (userId == null) {
            throw new IllegalArgumentException("缺少用户身份，请通过 Gateway 携带 Token 访问");
        }
    }

    private ChatConversationDTO toConversationDTO(ChatConversation conversation, Long currentUserId) {
        Long memberCount = memberRepository.countByConversationIdAndDeletedFalse(conversation.getId());

        ChatConversationMember currentMember =
                memberRepository.findByConversationIdAndUserIdAndDeletedFalse(conversation.getId(), currentUserId)
                        .orElse(null);

        Long lastReadMessageId = currentMember == null || currentMember.getLastReadMessageId() == null
                ? 0L
                : currentMember.getLastReadMessageId();

        Long unreadCount =
                messageRepository.countByConversationIdAndIdGreaterThanAndDeletedFalse(
                        conversation.getId(),
                        lastReadMessageId
                );

        Optional<ChatMessage> lastMessage =
                messageRepository.findTopByConversationIdAndDeletedFalseOrderByIdDesc(conversation.getId());

        return new ChatConversationDTO(
                conversation.getId(),
                conversation.getConversationType(),
                conversation.getTitle(),
                conversation.getWorkId(),
                conversation.getOwnerId(),
                conversation.getStatus(),
                memberCount,
                unreadCount,
                lastMessage.map(ChatMessage::getId).orElse(null),
                lastMessage.map(ChatMessage::getContent).orElse(null),
                lastMessage.map(ChatMessage::getCreatedAt).orElse(null),
                conversation.getCreatedAt(),
                conversation.getUpdatedAt()
        );
    }

    private ChatMessageDTO toMessageDTO(ChatMessage message) {
        List<Long> mentionUserIds =
                mentionRepository.findByMessageId(message.getId())
                        .stream()
                        .map(ChatMessageMention::getMentionedUserId)
                        .toList();

        return new ChatMessageDTO(
                message.getId(),
                message.getConversationId(),
                message.getSenderId(),
                message.getClientMessageId(),
                message.getMessageType(),
                message.getContent(),
                message.getMediaUrl(),
                message.getReplyToMessageId(),
                message.getDeleted(),
                message.getReviewStatus(),
                mentionUserIds,
                message.getCreatedAt()
        );
    }

    private void saveModerationLog(Long operatorId,
                                   Long conversationId,
                                   Long targetUserId,
                                   Long messageId,
                                   String operationType,
                                   String reason) {
        ChatModerationLog log = new ChatModerationLog();
        log.setOperatorId(operatorId);
        log.setConversationId(conversationId);
        log.setTargetUserId(targetUserId);
        log.setMessageId(messageId);
        log.setOperationType(operationType);
        log.setReason(reason);

        moderationLogRepository.save(log);
    }

    private String getReason(AdminActionRequest request, String defaultReason) {
        if (request == null || request.reason() == null || request.reason().isBlank()) {
            return defaultReason;
        }

        return request.reason().trim();
    }

    private ChatModerationLogDTO toModerationLogDTO(ChatModerationLog log) {
        return new ChatModerationLogDTO(
                log.getId(),
                log.getOperatorId(),
                log.getConversationId(),
                log.getTargetUserId(),
                log.getMessageId(),
                log.getOperationType(),
                log.getReason(),
                log.getCreatedAt()
        );
    }

    private List<Long> saveMentionsAndReturnValidUserIds(Long messageId,
                                                         Long conversationId,
                                                         Long senderId,
                                                         List<Long> mentionUserIds) {
        if (mentionUserIds == null || mentionUserIds.isEmpty()) {
            return List.of();
        }

        Set<Long> uniqueIds = new LinkedHashSet<>(mentionUserIds);
        List<Long> validIds = new ArrayList<>();

        for (Long mentionedUserId : uniqueIds) {
            if (mentionedUserId == null) {
                continue;
            }

            if (Objects.equals(mentionedUserId, senderId)) {
                continue;
            }

            boolean isMember =
                    memberRepository.existsByConversationIdAndUserIdAndDeletedFalse(
                            conversationId,
                            mentionedUserId
                    );

            if (!isMember) {
                continue;
            }

            ChatMessageMention mention = new ChatMessageMention();
            mention.setMessageId(messageId);
            mention.setMentionedUserId(mentionedUserId);
            mentionRepository.save(mention);

            validIds.add(mentionedUserId);
        }

        return validIds;
    }

    private void pushChatMessageToMembers(Long conversationId, ChatMessageDTO dto) {
        List<Long> memberIds = memberRepository.findActiveUserIdsByConversationId(conversationId);
        chatWebSocketHandler.pushChatMessageToUsers(memberIds, dto);
    }

    private void pushMessageDeletedToMembers(Long conversationId, Long messageId) {
        List<Long> memberIds = memberRepository.findActiveUserIdsByConversationId(conversationId);
        chatWebSocketHandler.pushMessageDeletedToUsers(memberIds, conversationId, messageId);
    }

    private void sendMentionNotifications(Long senderId,
                                          ChatConversation conversation,
                                          ChatMessage message,
                                          List<Long> mentionUserIds) {
        if (mentionUserIds == null || mentionUserIds.isEmpty()) {
            return;
        }

        for (Long mentionedUserId : mentionUserIds) {
            try {
                NotificationCreateRequest request = new NotificationCreateRequest(
                        mentionedUserId,
                        senderId,
                        "你在群聊中被提及",
                        "你在「" + conversation.getTitle() + "」中被提及：" + safePreview(message.getContent()),
                        "MENTION",
                        "CHAT_MESSAGE",
                        message.getId()
                );

                ApiResponse<String> response = notificationClient.createNotification(request);

                System.out.println("[聊天@通知] receiverId=" + mentionedUserId + ", response=" + response.getMessage());
            } catch (Exception e) {
                // 重要：通知失败不能影响聊天消息发送
                System.err.println("[聊天@通知失败] receiverId=" + mentionedUserId + ", error=" + e.getMessage());
            }
        }
    }

    private String safePreview(String content) {
        if (content == null || content.isBlank()) {
            return "";
        }

        String text = content.trim();

        if (text.length() > 50) {
            return text.substring(0, 50) + "...";
        }

        return text;
    }
}