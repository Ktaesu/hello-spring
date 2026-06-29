package com.ts.demo.hello_spring.member.dto;

import lombok.Data;

@Data
public class LoginRequestDTO {
    private String memberId;
    private String memberPwd;
}
