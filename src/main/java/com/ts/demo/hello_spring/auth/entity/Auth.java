package com.ts.demo.hello_spring.auth.entity;

import com.ts.demo.hello_spring.auth.type.AuthType;
import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;


import java.time.LocalDateTime;

@Entity
@Getter @Setter
@Table(name = "TB_COMMON_AUTH")
@NoArgsConstructor
public class Auth {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "AUTH_SEQ")
    @SequenceGenerator(name = "AUTH_SEQ", sequenceName = "SEQ_AUTH_ID", allocationSize = 1)
    private Long authId;

    private String identifier; // 이메일 혹은 전화번호

    @Enumerated(EnumType.STRING)
    private AuthType authType;

    private String authCode;
    private LocalDateTime expiredAt;
    private int requestCount;
    private int failCount;
    private String isVerified = "N";

    @Builder // 빌더 패턴을 쓰면 객체 생성이 편해집니다
    public Auth(String identifier, AuthType authType, String authCode, LocalDateTime expiredAt) {
        this.identifier = identifier;
        this.authType = authType;
        this.authCode = authCode;
        this.expiredAt = expiredAt;
        this.requestCount = 1;
        this.failCount = 0;
        this.isVerified = "N";
    }
}
