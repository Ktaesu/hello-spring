package com.ts.demo.hello_spring.common.security.customuser;

import lombok.Getter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.User;

import java.util.Collection;

@Getter
public class CustomUserDetails extends User {

    private final String nickname; // 우리가 추가하고 싶은 데이터

    public CustomUserDetails(String username, String password,
                             Collection<? extends GrantedAuthority> authorities,
                             String nickname) {
        super(username, password, authorities);
        this.nickname = nickname;
    }
}
