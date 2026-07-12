package com.yn.anime.notification.controller;

import com.yn.anime.common.dto.BroadcastNotificationRequest;
import com.yn.anime.common.dto.BroadcastResultDTO;
import com.yn.anime.common.dto.NotificationCreateRequest;
import com.yn.anime.common.dto.NotificationDTO;
import com.yn.anime.common.response.ApiResponse;
import com.yn.anime.notification.service.NotificationService;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/notifications/admin")
public class AdminNotificationController {
    private final NotificationService notificationService;

    public AdminNotificationController(NotificationService notificationService) {
        this.notificationService = notificationService;
    }

    /**
     * 管理员给指定用户发送通知。
     */
    @PostMapping("/users/{receiverId}")
    public ApiResponse<NotificationDTO> sendToUser(@RequestHeader("X-User-Id") Long adminId,
                                                   @PathVariable Long receiverId,
                                                   @RequestBody NotificationCreateRequest request) {
        return ApiResponse.success(notificationService.adminSendToUser(adminId, receiverId, request));
    }

    /**
     * 管理员发布系统公告，广播给所有正常用户。
     */
    @PostMapping("/broadcast")
    public ApiResponse<BroadcastResultDTO> broadcast(@RequestHeader("X-User-Id") Long adminId,
                                                     @RequestBody BroadcastNotificationRequest request) {
        return ApiResponse.success(notificationService.broadcastToAll(adminId, request));
    }

    /**
     * 管理员删除整条通知内容。
     * 删除后所有用户都看不到这条通知。
     */
    @DeleteMapping("/messages/{messageId}")
    public ApiResponse<String> deleteMessage(@RequestHeader("X-User-Id") Long adminId,
                                             @PathVariable Long messageId) {
        notificationService.adminDeleteMessage(adminId, messageId);
        return ApiResponse.success("通知已被管理员删除");
    }

    /**
     * 管理员删除某个用户收到的一条通知。
     */
    @DeleteMapping("/user-messages/{userMessageId}")
    public ApiResponse<String> deleteUserMessage(@RequestHeader("X-User-Id") Long adminId,
                                                 @PathVariable Long userMessageId) {
        notificationService.adminDeleteUserMessage(adminId, userMessageId);
        return ApiResponse.success("用户通知已被管理员删除");
    }
}