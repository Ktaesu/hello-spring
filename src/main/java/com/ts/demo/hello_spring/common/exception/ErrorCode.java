package com.ts.demo.hello_spring.common.exception;

import lombok.Getter;

@Getter
public enum ErrorCode {

    MEMBER_NOT_FOUND("M001", "존재하지 않는 회원입니다."),
    INVALID_PASSWORD("M002", "아이디 또는 비밀번호가 일치하지 않습니다.");

    private final String code;
    private final String message;

    ErrorCode(String code, String message) {
        this.code = code;
        this.message = message;
    }

}
