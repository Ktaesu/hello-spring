package com.ts.demo.hello_spring.common.security.jwt;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.util.Date;

@Component
public class JwtProvider {

    private final String secretKey;
    private final long tokenValidTime;
    private Key key;

    // application.properties에서 값을 읽어옴
    public JwtProvider(
            @Value("${jwt.secret}") String secretKey,
            @Value("${jwt.expiration-time}") long tokenValidTime) {
        this.secretKey = secretKey;
        this.tokenValidTime = tokenValidTime;
    }

    // 빈 초기화 시 Key 객체 생성
    @PostConstruct
    protected void init() {
        this.key = Keys.hmacShaKeyFor(secretKey.getBytes(StandardCharsets.UTF_8));
    }

    // 1. 토큰 생성
    public String createToken(String memberId) {
        Claims claims = Jwts.claims().setSubject(memberId);
        Date now = new Date();

        return Jwts.builder()
                .setClaims(claims)
                .setIssuedAt(now)
                .setExpiration(new Date(now.getTime() + tokenValidTime))
                .signWith(key, SignatureAlgorithm.HS256)
                .compact();
    }

    // 2. 토큰 유효성 및 만료일자 확인
    public boolean validateToken(String token) {
        try {
            Jws<Claims> claims = Jwts.parserBuilder()
                    .setSigningKey(key)
                    .build()
                    .parseClaimsJws(token);
            return !claims.getBody().getExpiration().before(new Date());
        } catch (Exception e) {
            return false;
        }
    }

    // 3. 토큰에서 회원 아이디(Subject) 추출
    public String getMemberId(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(key)
                .build()
                .parseClaimsJws(token)
                .getBody()
                .getSubject();
    }
}
