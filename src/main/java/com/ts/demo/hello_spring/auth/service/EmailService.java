package com.ts.demo.hello_spring.auth.service;

import com.ts.demo.hello_spring.auth.entity.Auth;
import com.ts.demo.hello_spring.auth.repository.AuthRepository;
import com.ts.demo.hello_spring.auth.type.AuthType;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.Random;

@Service("emailService")
@RequiredArgsConstructor
@Transactional
public class EmailService implements AuthService{

    // 스프링 부트가 설정 파일을 읽어 자동으로 생성해주는 객체입니다.
    private final AuthRepository authRepository;
    private final JavaMailSender mailSender;

    @Value("${spring.mail.username}")
    private String fromEmail;

    /**
     * 6자리 인증번호 생성 (111111 ~ 999999)
     */
    private String createRandomCode() {
        return String.format("%06d", new Random().nextInt(1000000));
    }

    public void sendAuthCode(String email) {
        String code = createRandomCode();
        LocalDateTime expireTime = LocalDateTime.now().plusMinutes(10); // 메일은 조금 넉넉히 10분

        // [현업 로직] 기존 기록 확인 (identifier와 EMAIL 타입으로 조회)
        Optional<Auth> existingAuth = authRepository.findByIdentifierAndAuthType(email, AuthType.EMAIL);

        if (existingAuth.isPresent()) {
            Auth auth = existingAuth.get();
            if (auth.getRequestCount() >= 10) { // 이메일은 횟수를 좀 더 줘도 됩니다.
                throw new RuntimeException("일일 인증 요청 횟수를 초과했습니다.");
            }
            auth.setAuthCode(code);
            auth.setExpiredAt(expireTime);
            auth.setRequestCount(auth.getRequestCount() + 1);
            auth.setIsVerified("N");
        } else {
            Auth newAuth = Auth.builder()
                    .identifier(email)
                    .authType(AuthType.EMAIL)
                    .authCode(code)
                    .expiredAt(expireTime)
                    .build();
            authRepository.save(newAuth);
        }

        // 실제 메일 발송
        sendRealEmail(email, code);
    }

    // 2. 이메일 검증
    public boolean verifyCode(String email, String code) {
        Auth auth = authRepository.findByIdentifierAndAuthType(email, AuthType.EMAIL)
                .orElseThrow(() -> new RuntimeException("인증 요청 기록이 없습니다."));

        if (auth.getExpiredAt().isBefore(LocalDateTime.now())) {
            throw new RuntimeException("인증 시간이 만료되었습니다.");
        }

        if (auth.getAuthCode().equals(code)) {
            auth.setIsVerified("Y");
            return true;
        }
        return false;
    }

    // 실제 JavaMail 발송 로직
    private void sendRealEmail(String to, String code) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom(fromEmail);
        message.setTo(to);
        message.setSubject("[HelloSpring] 이메일 인증 번호입니다.");
        message.setText("인증 번호는 [" + code + "] 입니다. 10분 안에 입력해주세요.");
        mailSender.send(message);
    }
}
