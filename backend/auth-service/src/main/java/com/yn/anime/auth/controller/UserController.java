package com.yn.anime.auth.controller;

import com.yn.anime.auth.service.AuthUserService;
import com.yn.anime.common.dto.*;
import com.yn.anime.common.response.ApiResponse;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;
import java.util.List;

@RestController
@RequestMapping("/api/users")
public class UserController {
    private final AuthUserService authUserService;

    public UserController(AuthUserService authUserService) {
        this.authUserService = authUserService;
    }

    @GetMapping("/me")
    public ApiResponse<UserProfileDTO> getMe(@RequestHeader("Authorization") String authorization) {
        return ApiResponse.success(authUserService.getMe(authorization));
    }

    @PutMapping("/me")
    public ApiResponse<UserProfileDTO> updateMe(@RequestHeader("Authorization") String authorization,
                                                @RequestBody UpdateProfileRequest request) {
        return ApiResponse.success(authUserService.updateMe(authorization, request));
    }

    @PutMapping("/me/password")
    public ApiResponse<String> changePassword(@RequestHeader("Authorization") String authorization,
                                              @RequestBody ChangePasswordRequest request) {
        authUserService.changePassword(authorization, request);
        return ApiResponse.success("密码修改成功");
    }

    @GetMapping("/{id}")
    public UserDTO getUserById(@PathVariable Long id) {
        return authUserService.getUserById(id);
    }

    @GetMapping("/{id}/status")
    public UserStatusDTO getUserStatus(@PathVariable Long id) {
        return authUserService.getUserStatus(id);
    }

    @GetMapping("/batch")
    public List<UserDTO> batchUsers(@RequestParam String ids) {
        List<Long> idList = Arrays.stream(ids.split(","))
                .map(String::trim)
                .filter(s -> !s.isBlank())
                .map(Long::valueOf)
                .toList();

        return authUserService.batchUsers(idList);
    }

    @GetMapping("/internal/active-user-ids")
    public List<Long> listActiveUserIds() {
        return authUserService.listActiveUserIds();
    }
}