package com.yn.anime.chat.controller;

import com.yn.anime.chat.dto.AdminActionRequest;
import com.yn.anime.chat.dto.ChatConversationDTO;
import com.yn.anime.chat.dto.ChatMessageDTO;
import com.yn.anime.chat.dto.ChatModerationLogDTO;
import com.yn.anime.chat.service.ChatService;
import com.yn.anime.common.response.ApiResponse;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/chats/admin")
public class AdminChatController {
    private final ChatService chatService;

    public AdminChatController(ChatService chatService) {
        this.chatService = chatService;
    }

    @GetMapping("/conversations")
    public ApiResponse<List<ChatConversationDTO>> listConversations(
            @RequestHeader("X-User-Id") Long adminId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "50") int size
    ) {
        return ApiResponse.success(chatService.adminListConversations(adminId, page, size));
    }

    @GetMapping("/conversations/{conversationId}/messages")
    public ApiResponse<List<ChatMessageDTO>> listMessages(
            @RequestHeader("X-User-Id") Long adminId,
            @PathVariable Long conversationId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "50") int size
    ) {
        return ApiResponse.success(chatService.adminListMessages(adminId, conversationId, page, size));
    }

    @DeleteMapping("/messages/{messageId}")
    public ApiResponse<String> deleteMessage(
            @RequestHeader("X-User-Id") Long adminId,
            @PathVariable Long messageId,
            @RequestBody(required = false) AdminActionRequest request
    ) {
        return ApiResponse.success(chatService.adminDeleteMessage(adminId, messageId, request));
    }

    @PutMapping("/conversations/{conversationId}/mute/{targetUserId}")
    public ApiResponse<String> muteUser(
            @RequestHeader("X-User-Id") Long adminId,
            @PathVariable Long conversationId,
            @PathVariable Long targetUserId,
            @RequestBody(required = false) AdminActionRequest request
    ) {
        return ApiResponse.success(chatService.adminMuteUser(adminId, conversationId, targetUserId, request));
    }

    @PutMapping("/conversations/{conversationId}/unmute/{targetUserId}")
    public ApiResponse<String> unmuteUser(
            @RequestHeader("X-User-Id") Long adminId,
            @PathVariable Long conversationId,
            @PathVariable Long targetUserId,
            @RequestBody(required = false) AdminActionRequest request
    ) {
        return ApiResponse.success(chatService.adminUnmuteUser(adminId, conversationId, targetUserId, request));
    }

    @PutMapping("/conversations/{conversationId}/close")
    public ApiResponse<String> closeConversation(
            @RequestHeader("X-User-Id") Long adminId,
            @PathVariable Long conversationId,
            @RequestBody(required = false) AdminActionRequest request
    ) {
        return ApiResponse.success(chatService.adminCloseConversation(adminId, conversationId, request));
    }

    @PutMapping("/conversations/{conversationId}/disband")
    public ApiResponse<String> disbandConversation(
            @RequestHeader("X-User-Id") Long adminId,
            @PathVariable Long conversationId,
            @RequestBody(required = false) AdminActionRequest request
    ) {
        return ApiResponse.success(chatService.adminDisbandConversation(adminId, conversationId, request));
    }

    @GetMapping("/logs")
    public ApiResponse<List<ChatModerationLogDTO>> listLogs(
            @RequestHeader("X-User-Id") Long adminId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "50") int size
    ) {
        return ApiResponse.success(chatService.adminListLogs(adminId, page, size));
    }
}