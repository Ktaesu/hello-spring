package com.ts.demo.hello_spring.common.exception;

import com.ts.demo.hello_spring.common.api.ApiResponse;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

    // BusinessException은 errorCode를 가지고 있으므로 더 구체적으로 처리

    @ExceptionHandler(BusinessException.class)
    public ApiResponse<String> handleBusinessException(BusinessException e) {
        return ApiResponse.fail("[" + e.getErrorCode().getCode() + "] " + e.getMessage());
    }

    @ExceptionHandler(RuntimeException.class)
    public ApiResponse<String> handleRuntimeException(RuntimeException e) {
        return ApiResponse.fail(e.getMessage()); // "일일 인증 횟수를 초과했습니다..." 출력
    }
}
