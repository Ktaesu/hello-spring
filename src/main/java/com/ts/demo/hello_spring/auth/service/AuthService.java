package com.ts.demo.hello_spring.auth.service;

public interface AuthService {

    // 인증번호 발송 (휴대폰 번호나 이메일 주소를 받음)
    void sendAuthCode(String identifier);

    // 인증번호 확인
    boolean verifyCode(String identifier, String code);
}
