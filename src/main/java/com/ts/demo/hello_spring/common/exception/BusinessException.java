package com.ts.demo.hello_spring.common.exception;

import lombok.Getter;

/**
 * 비즈니스 로직에서 발생하는 예외.
 * 단순 메시지 대신 ErrorCode를 함께 담아서,
 * 어떤 상황에서 실패했는지 코드만으로 추적할 수 있도록 한다.
 */

@Getter
public class BusinessException extends RuntimeException{

    private final ErrorCode errorCode;

    public BusinessException(ErrorCode errorCode) {
        super(errorCode.getMessage());
        this.errorCode = errorCode;
    }
}
