package com.ts.demo.hello_spring.member.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class LoginResponseDTO {

    private String memberId;
    private  String memberName;
}
