package com.yn.anime.auth.controller;

import com.yn.anime.auth.service.AuthUserService;
import com.yn.anime.common.dto.LoginRequest;
import com.yn.anime.common.dto.LoginResponse;
import com.yn.anime.common.dto.RegisterRequest;
import com.yn.anime.common.dto.RegisterResponse;
import com.yn.anime.common.dto.TokenCheckResponse;
import com.yn.anime.common.response.ApiResponse;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
public class AuthController {
    private final AuthUserService authUserService;

    public AuthController(AuthUserService authUserService) {
        this.authUserService = authUserService;
    }

    @GetMapping("/ping")
    public String ping() {
        return "auth-service is running";
    }

    @PostMapping("/register")
    public ApiResponse<RegisterResponse> register(@RequestBody RegisterRequest request) {
        return ApiResponse.success(authUserService.register(request));
    }

    @PostMapping("/login")
    public ApiResponse<LoginResponse> login(@RequestBody LoginRequest request) {
        return ApiResponse.success(authUserService.login(request));
    }

    @GetMapping("/check")
    public ApiResponse<TokenCheckResponse> check(@RequestHeader("Authorization") String authorization) {
        return ApiResponse.success(authUserService.checkToken(authorization));
    }
}