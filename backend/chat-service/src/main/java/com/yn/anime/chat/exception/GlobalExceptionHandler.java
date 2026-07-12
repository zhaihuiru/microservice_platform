package com.yn.anime.chat.exception;

import com.yn.anime.common.response.ApiResponse;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {
    @ExceptionHandler(IllegalArgumentException.class)
    public ApiResponse<Object> handleIllegalArgumentException(IllegalArgumentException e) {
        return new ApiResponse<>(400, e.getMessage(), null);
    }

    @ExceptionHandler(IllegalStateException.class)
    public ApiResponse<Object> handleIllegalStateException(IllegalStateException e) {
        return new ApiResponse<>(400, e.getMessage(), null);
    }

    @ExceptionHandler(Exception.class)
    public ApiResponse<Object> handleException(Exception e) {
        e.printStackTrace();
        return ApiResponse.fail("聊天服务异常：" + e.getMessage());
    }
}