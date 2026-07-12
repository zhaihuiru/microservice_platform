package com.yn.anime.auth.handler;

import com.yn.anime.common.response.ApiResponse;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {
    @ExceptionHandler(IllegalArgumentException.class)
    public ApiResponse<String> handleIllegalArgumentException(IllegalArgumentException e) {
        return ApiResponse.fail(e.getMessage());
    }

    @ExceptionHandler(IllegalStateException.class)
    public ApiResponse<String> handleIllegalStateException(IllegalStateException e) {
        return ApiResponse.fail(e.getMessage());
    }

    @ExceptionHandler(Exception.class)
    public ApiResponse<String> handleException(Exception e) {
        return ApiResponse.fail("系统异常：" + e.getMessage());
    }
}