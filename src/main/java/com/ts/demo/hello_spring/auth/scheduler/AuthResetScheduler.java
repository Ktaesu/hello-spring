package com.ts.demo.hello_spring.auth.scheduler;

import com.ts.demo.hello_spring.auth.repository.AuthRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@RequiredArgsConstructor
public class AuthResetScheduler {

    private final AuthRepository authRepository;

    // 초 분 시 일 월 요일 (매일 0시 0분 0초)
    @Scheduled(cron = "0 0 0 * * *")
    @Transactional
    public void resetSmsCount() {
        authRepository.resetAllRequestCounts();
        System.out.println("인증 요청 횟수가 초기화되었습니다.");
    }
}
