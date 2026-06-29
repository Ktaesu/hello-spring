package com.ts.demo.hello_spring.auth.repository;

import com.ts.demo.hello_spring.auth.entity.Auth;
import com.ts.demo.hello_spring.auth.type.AuthType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

public interface AuthRepository extends JpaRepository<Auth, Long> {

    // 이메일이나 전화번호 + 타입으로 가장 최근 인증 정보를 찾기 위함
    Optional<Auth> findByIdentifierAndAuthType(String identifier, AuthType authType);

    // 오늘 하루 동안 몇 번이나 요청했는지 카운트할 때 필요 (보안/비용 절감)
    // LocalDateTime.now().with(LocalTime.MIN) 등을 활용해 쿼리할 수 있습니다.

    @Modifying
    @Query("UPDATE Auth a SET a.requestCount = 0")
    void resetAllRequestCounts();
}
