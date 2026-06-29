package com.ts.demo.hello_spring.common.security.config;

import com.ts.demo.hello_spring.common.security.customuser.CustomUserDetailsService;
import com.ts.demo.hello_spring.common.security.jwt.JwtAuthenticationEntryPoint;
import com.ts.demo.hello_spring.common.security.jwt.JwtFilter;
import com.ts.demo.hello_spring.common.security.jwt.JwtProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.autoconfigure.security.servlet.PathRequest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityCustomizer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final JwtProvider jwtProvider;
    private final JwtAuthenticationEntryPoint jwtAuthenticationEntryPoint; // 주입 추가
    private final CustomUserDetailsService customUserDetailsService;


    @Bean
    public BCryptPasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public WebSecurityCustomizer webSecurityCustomizer() {
        return (web) -> web.ignoring()
                .requestMatchers(PathRequest.toStaticResources().atCommonLocations());
        // resources/static 내의 css, js, images 등을 알아서 제외해줌
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .csrf(csrf -> csrf.disable()) // CSRF 일단 비활성화

                .sessionManagement(session -> session
                        .sessionCreationPolicy(SessionCreationPolicy.STATELESS)) // 세션 사용 안함(JWT 방식)

                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/", "/member/**", "/api/auth/**", "/product/api/**").permitAll() // 로그인 없이 허용할 경로
                        .anyRequest().authenticated() // 나머지는 인증 필요
                )

                // UsernamePasswordAuthenticationFilter 실행 전에 우리가 만든 JwtFilter를 먼저 실행해라!
                .addFilterBefore(new JwtFilter(jwtProvider, customUserDetailsService),
                        UsernamePasswordAuthenticationFilter.class)

                .exceptionHandling(exception -> exception
                        .authenticationEntryPoint(jwtAuthenticationEntryPoint) // 이 부분 추가
                );

        return http.build();
    }
}
