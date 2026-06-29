package com.ts.demo.hello_spring.auth.service;

import com.ts.demo.hello_spring.auth.entity.Auth;
import com.ts.demo.hello_spring.auth.repository.AuthRepository;
import com.ts.demo.hello_spring.auth.type.AuthType;
import lombok.RequiredArgsConstructor;
import net.nurigo.sdk.message.model.Message;
import net.nurigo.sdk.message.request.SingleMessageSendingRequest;
import net.nurigo.sdk.message.service.DefaultMessageService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.Random;

@Service("smsService")
@RequiredArgsConstructor
@Transactional // DB 저장을 위해 트랜잭션 추가
public class SmsService implements AuthService{
    private final AuthRepository authRepository;
    private final DefaultMessageService messageService;

    @Value("${coolsms.from.number}")
    private String fromNumber;

    // 1. 인증번호 발송 및 DB 기록
    public void sendAuthCode(String phone) {
        String code = createRandomCode();
        LocalDateTime expireTime = LocalDateTime.now().plusMinutes(3); // 3분 유효

        // 기존에 보낸 기록이 있는지 확인
        Optional<Auth> existingAuth = authRepository.findByIdentifierAndAuthType(phone, AuthType.SMS);

        if (existingAuth.isPresent()) {
            Auth auth = existingAuth.get();

            // 하루 발송 제한 (예: 5회)
            if (auth.getRequestCount() >= 5) {
                throw new RuntimeException("일일 인증 횟수를 초과했습니다. (최대 5회)");
            }

            // 기존 레코드 업데이트 (재발송)
            auth.setAuthCode(code);
            auth.setExpiredAt(expireTime);
            auth.setRequestCount(auth.getRequestCount() + 1);
            auth.setIsVerified("N");
        } else {
            // 처음 발송 시 새 레코드 생성
            Auth newAuth = Auth.builder()
                    .identifier(phone)
                    .authType(AuthType.SMS)
                    .authCode(code)
                    .expiredAt(expireTime)
                    .build();
            authRepository.save(newAuth);
        }

        // 실제 SMS 발송
        this.sendSms(phone, "[HelloSpring] 인증번호는 [" + code + "] 입니다.");
    }

    // 2. 인증번호 확인
    public boolean verifyCode(String phone, String code) {
        Auth auth = authRepository.findByIdentifierAndAuthType(phone, AuthType.SMS)
                .orElseThrow(() -> new RuntimeException("인증 요청 기록이 없습니다."));

        if (auth.getExpiredAt().isBefore(LocalDateTime.now())) {
            throw new RuntimeException("인증 시간이 만료되었습니다.");
        }

        if (auth.getAuthCode().equals(code)) {
            auth.setIsVerified("Y");
            return true;
        }

        auth.setFailCount(auth.getFailCount() + 1);
        return false;
    }

    // 6자리 난수 생성기
    private String createRandomCode() {
        Random random = new Random();
        return String.format("%06d", random.nextInt(1000000));
    }

    // CoolSMS 전송 로직 (내부 메서드)
    private void sendSms(String to, String content) {
        // 1. 콘솔에 찍어서 인증번호 확인 (DB 안 열어봐도 되게!)
        System.out.println("========================================");
        System.out.println("[SMS 발송 시뮬레이션]");
        System.out.println("To: " + to);
        System.out.println("Content: " + content);
        System.out.println("========================================");
//        Message message = new Message();
//        message.setFrom(fromNumber);
//        message.setTo(to.replace("-", ""));
//        message.setText(content);
//        this.messageService.sendOne(new SingleMessageSendingRequest(message));
    }

}
