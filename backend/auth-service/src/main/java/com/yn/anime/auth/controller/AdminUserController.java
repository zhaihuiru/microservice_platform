package com.yn.anime.auth.controller;

import com.yn.anime.auth.entity.UserOperationLog;
import com.yn.anime.auth.service.AuthUserService;
import com.yn.anime.common.dto.*;
import com.yn.anime.common.response.ApiResponse;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin/users")
public class AdminUserController {
    private final AuthUserService authUserService;

    public AdminUserController(AuthUserService authUserService) {
        this.authUserService = authUserService;
    }

    @GetMapping
    public ApiResponse<Page<AdminUserDTO>> listUsers(@RequestHeader("Authorization") String authorization,
                                                     @RequestParam(defaultValue = "0") int page,
                                                     @RequestParam(defaultValue = "10") int size) {
        return ApiResponse.success(authUserService.listUsers(authorization, page, size));
    }

    @GetMapping("/{id}")
    public ApiResponse<AdminUserDTO> getUserDetail(@RequestHeader("Authorization") String authorization,
                                                   @PathVariable Long id) {
        return ApiResponse.success(authUserService.getAdminUserDetail(authorization, id));
    }

    @PutMapping("/{id}/ban")
    public ApiResponse<String> banUser(@RequestHeader("Authorization") String authorization,
                                       @RequestHeader(value = "X-Request-Id", required = false) String requestId,
                                       @PathVariable Long id,
                                       @RequestBody(required = false) AdminBanRequest request) {
        String reason = request == null ? null : request.reason();
        authUserService.banUser(authorization, id, reason, requestId);
        return ApiResponse.success("用户已封禁");
    }

    @PutMapping("/{id}/unban")
    public ApiResponse<String> unbanUser(@RequestHeader("Authorization") String authorization,
                                         @RequestHeader(value = "X-Request-Id", required = false) String requestId,
                                         @PathVariable Long id) {
        authUserService.unbanUser(authorization, id, requestId);
        return ApiResponse.success("用户已解封");
    }

    @PutMapping("/{id}/reset-password")
    public ApiResponse<String> resetPassword(@RequestHeader("Authorization") String authorization,
                                             @RequestHeader(value = "X-Request-Id", required = false) String requestId,
                                             @PathVariable Long id,
                                             @RequestBody AdminResetPasswordRequest request) {
        authUserService.resetPassword(authorization, id, request.newPassword(), requestId);
        return ApiResponse.success("密码已重置");
    }

    @PutMapping("/{id}/roles")
    public ApiResponse<String> assignRoles(@RequestHeader("Authorization") String authorization,
                                           @RequestHeader(value = "X-Request-Id", required = false) String requestId,
                                           @PathVariable Long id,
                                           @RequestBody AssignRoleRequest request) {
        authUserService.assignRoles(authorization, id, request, requestId);
        return ApiResponse.success("角色已更新");
    }

    @GetMapping("/{id}/logs")
    public ApiResponse<List<UserOperationLog>> getUserLogs(@RequestHeader("Authorization") String authorization,
                                                           @PathVariable Long id) {
        return ApiResponse.success(authUserService.getUserLogs(authorization, id));
    }
}