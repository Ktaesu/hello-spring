package com.ts.demo.hello_spring.member.service;

import com.ts.demo.hello_spring.member.dto.LoginRequestDTO;
import com.ts.demo.hello_spring.member.dto.LoginResponseDTO;
import com.ts.demo.hello_spring.member.dto.MemberDTO;

public interface MemberService {

    //로그인
    String login(LoginRequestDTO dto);

    //아이디 중복체크
    boolean checkIdDuplicate(String memberId);

    //닉네임 중복체크
    boolean checkNicknameDuplicate(String nickname);

    String sendPhoneAuthCode(String phone);

    //회원가입
    boolean joinMember(MemberDTO.JoinRequest joinDto);
}
