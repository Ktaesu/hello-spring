package com.ts.demo.hello_spring.auth.controller;

import com.ts.demo.hello_spring.auth.service.AuthService;
import com.ts.demo.hello_spring.common.api.ApiResponse;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthService smsService;
    private final AuthService emailService;

    // @RequiredArgsConstructor 대신 생성자를 직접 선언하면 @Qualifier를 쓸 수 있어 안전
    public AuthController(@Qualifier("smsService") AuthService smsService,
                          @Qualifier("emailService") AuthService emailService) {
        this.smsService = smsService;
        this.emailService = emailService;
    }

    // --- 휴대폰 인증 영역 ---
    // 인증번호 발송
    @PostMapping("/sms/send")
    public ApiResponse<String> sendSms(@RequestParam("phone") String phone) {
        smsService.sendAuthCode(phone);
            return ApiResponse.success("문자가 발송되었습니다.");
    }

    // 인증번호 확인
    @PostMapping("/sms/verify")
    public ApiResponse<Boolean> verifySms(@RequestParam("phone") String phone, @RequestParam("code") String code) {
        boolean isOk = smsService.verifyCode(phone, code);
        return isOk ? ApiResponse.success(true) : ApiResponse.fail("인증번호가 틀렸거나 만료되었습니다.");
    }

    // --- 이메일 인증 영역 ---
    @PostMapping("/email/send")
    public ApiResponse<String> sendEmail(@RequestParam("email") String email) {
        emailService.sendAuthCode(email); // SmsService와 유사한 구조
        return ApiResponse.success("인증 메일이 발송되었습니다.");
    }

    // 인증번호 확인
    @PostMapping("/email/verify")
    public ApiResponse<Boolean> verifyEmail(@RequestParam("email") String email, @RequestParam("code") String code) {
        boolean isOk = emailService.verifyCode(email, code);
        return isOk ? ApiResponse.success(true) : ApiResponse.fail("인증 코드가 일치하지 않습니다.");
    }
}
