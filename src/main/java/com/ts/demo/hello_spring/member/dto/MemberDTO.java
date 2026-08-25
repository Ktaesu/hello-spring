package com.ts.demo.hello_spring.member.dto;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

public class MemberDTO {

    @Getter
    @Setter
    @ToString
    public static class JoinRequest {
        private String memberId;
        private String memberName;
        private String memberPwd;
        private String nickname;
        private String phone;
        private String email;
        private String zipcode;        // 추가됨
        private String address;
        private String addressDetail;
        private String role;
    }

}
