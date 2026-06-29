package com.ts.demo.hello_spring.common.security.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ViewControllerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {
    @Override
    public void addViewControllers(ViewControllerRegistry registry) {
        // 1. "/" (루트) 경로로 접속하면
        // 2. "main" 이라는 이름의 뷰(templates/main.html)를 보여줘라!
        registry.addViewController("/").setViewName("main");

        // 추가로 필요한 페이지가 있다면 아래처럼 계속 늘려갈 수 있습니다.
        // registry.addViewController("/login").setViewName("auth/login");
        registry.addViewController("/member/agreeForm").setViewName("member/agreeForm");
        registry.addViewController("/member/createMember").setViewName("member/createMember");
    }
}
