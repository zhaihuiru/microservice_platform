package com.yn.anime.notification.controller;

import com.yn.anime.common.dto.NotificationCreateRequest;
import com.yn.anime.common.dto.NotificationDTO;
import com.yn.anime.common.response.ApiResponse;
import com.yn.anime.notification.service.NotificationService;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/notifications")
public class NotificationController {
    private final NotificationService notificationService;

    public NotificationController(NotificationService notificationService) {
        this.notificationService = notificationService;
    }

    @GetMapping("/ping")
    public String ping() {
        return "notification-service is running";
    }

    /**
     * 保留原来的接口，方便 comment-service / 其他服务直接 Feign 调用。
     * 注意：服务间 Feign 调用一般走 notification-service 服务名，不经过 Gateway。
     */
    @PostMapping
    public String createNotification(@RequestBody NotificationCreateRequest request) {
        NotificationDTO dto = notificationService.sendToUser(request);
        return "通知创建成功，接收人：" + dto.receiverId();
    }

    /**
     * 查询当前登录用户的通知。
     * X-User-Id 是 Gateway 鉴权后透传过来的。
     */
    @GetMapping("/my")
    public ApiResponse<Page<NotificationDTO>> listMyNotifications(
            @RequestHeader("X-User-Id") Long userId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        return ApiResponse.success(notificationService.listMyNotifications(userId, page, size));
    }

    @GetMapping("/my/unread-count")
    public ApiResponse<Long> unreadCount(@RequestHeader("X-User-Id") Long userId) {
        return ApiResponse.success(notificationService.countUnread(userId));
    }

    @PutMapping("/{userMessageId}/read")
    public ApiResponse<NotificationDTO> markRead(@RequestHeader("X-User-Id") Long userId,
                                                 @PathVariable Long userMessageId) {
        return ApiResponse.success(notificationService.markRead(userId, userMessageId));
    }

    @PutMapping("/my/read-all")
    public ApiResponse<String> readAll(@RequestHeader("X-User-Id") Long userId) {
        int count = notificationService.readAll(userId);
        return ApiResponse.success("已标记 " + count + " 条通知为已读");
    }

    @DeleteMapping("/{userMessageId}")
    public ApiResponse<String> deleteMyNotification(@RequestHeader("X-User-Id") Long userId,
                                                    @PathVariable Long userMessageId) {
        notificationService.deleteMyNotification(userId, userMessageId);
        return ApiResponse.success("通知已删除");
    }
}