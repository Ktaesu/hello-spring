package com.ts.demo.hello_spring.common.exception;

import com.ts.demo.hello_spring.common.api.ApiResponse;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {
    @ExceptionHandler(RuntimeException.class)
    public ApiResponse<String> handleRuntimeException(RuntimeException e) {
        return ApiResponse.fail(e.getMessage()); // "일일 인증 횟수를 초과했습니다..." 출력
    }
}
