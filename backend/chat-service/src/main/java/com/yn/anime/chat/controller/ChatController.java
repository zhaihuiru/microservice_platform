package com.yn.anime.chat.controller;

import com.yn.anime.chat.dto.*;
import com.yn.anime.chat.service.ChatService;
import com.yn.anime.common.response.ApiResponse;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/chats")
public class ChatController {
    private final ChatService chatService;

    public ChatController(ChatService chatService) {
        this.chatService = chatService;
    }

    @GetMapping("/ping")
    public ApiResponse<String> ping() {
        return ApiResponse.success("chat-service is running");
    }

    @PostMapping("/conversations/private")
    public ApiResponse<ChatConversationDTO> createPrivateConversation(
            @RequestHeader("X-User-Id") Long currentUserId,
            @RequestBody CreatePrivateConversationRequest request
    ) {
        return ApiResponse.success(chatService.createPrivateConversation(currentUserId, request));
    }

    @PostMapping("/conversations/group")
    public ApiResponse<ChatConversationDTO> createGroupConversation(
            @RequestHeader("X-User-Id") Long currentUserId,
            @RequestBody CreateGroupConversationRequest request
    ) {
        return ApiResponse.success(chatService.createGroupConversation(currentUserId, request));
    }

    @PostMapping("/conversations/work")
    public ApiResponse<ChatConversationDTO> createWorkConversation(
            @RequestHeader("X-User-Id") Long currentUserId,
            @RequestBody CreateWorkConversationRequest request
    ) {
        return ApiResponse.success(chatService.createWorkConversation(currentUserId, request));
    }

    @PostMapping("/conversations/{conversationId}/members")
    public ApiResponse<ChatConversationDTO> addMembers(
            @RequestHeader("X-User-Id") Long currentUserId,
            @PathVariable Long conversationId,
            @RequestBody AddMemberRequest request
    ) {
        return ApiResponse.success(chatService.addMembers(currentUserId, conversationId, request));
    }

    @PostMapping("/conversations/{conversationId}/join")
    public ApiResponse<ChatConversationDTO> joinConversation(
            @RequestHeader("X-User-Id") Long currentUserId,
            @PathVariable Long conversationId
    ) {
        return ApiResponse.success(chatService.joinConversation(currentUserId, conversationId));
    }

    @PostMapping("/conversations/{conversationId}/leave")
    public ApiResponse<String> leaveConversation(
            @RequestHeader("X-User-Id") Long currentUserId,
            @PathVariable Long conversationId
    ) {
        return ApiResponse.success(chatService.leaveConversation(currentUserId, conversationId));
    }

    @GetMapping("/conversations/my")
    public ApiResponse<List<ChatConversationDTO>> listMyConversations(
            @RequestHeader("X-User-Id") Long currentUserId
    ) {
        return ApiResponse.success(chatService.listMyConversations(currentUserId));
    }

    @GetMapping("/conversations/{conversationId}/messages")
    public ApiResponse<List<ChatMessageDTO>> listMessages(
            @RequestHeader("X-User-Id") Long currentUserId,
            @PathVariable Long conversationId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "50") int size
    ) {
        return ApiResponse.success(chatService.listMessages(currentUserId, conversationId, page, size));
    }

    @PostMapping("/conversations/{conversationId}/messages")
    public ApiResponse<ChatMessageDTO> sendMessage(
            @RequestHeader("X-User-Id") Long currentUserId,
            @PathVariable Long conversationId,
            @RequestBody SendChatMessageRequest request
    ) {
        return ApiResponse.success(chatService.sendMessage(currentUserId, conversationId, request));
    }

    @PutMapping("/conversations/{conversationId}/read")
    public ApiResponse<String> markConversationRead(
            @RequestHeader("X-User-Id") Long currentUserId,
            @PathVariable Long conversationId
    ) {
        return ApiResponse.success(chatService.markConversationRead(currentUserId, conversationId));
    }

    @DeleteMapping("/messages/{messageId}")
    public ApiResponse<String> deleteMyMessage(
            @RequestHeader("X-User-Id") Long currentUserId,
            @PathVariable Long messageId
    ) {
        return ApiResponse.success(chatService.deleteMyMessage(currentUserId, messageId));
    }
}