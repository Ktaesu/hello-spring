package com.ts.demo.hello_spring.member.service;

import com.ts.demo.hello_spring.common.exception.BusinessException;
import com.ts.demo.hello_spring.common.exception.ErrorCode;
import com.ts.demo.hello_spring.common.security.jwt.JwtProvider;
import com.ts.demo.hello_spring.member.dto.LoginRequestDTO;
import com.ts.demo.hello_spring.member.dto.MemberDTO;
import com.ts.demo.hello_spring.member.entity.Member;
import com.ts.demo.hello_spring.member.entity.MemberAddress;
import com.ts.demo.hello_spring.member.repository.MemberAddressRepository;
import com.ts.demo.hello_spring.member.repository.MemberRepository;
import com.ts.demo.hello_spring.auth.service.SmsService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class MemberServiceImple implements MemberService{

    private final MemberRepository memberRepository;
    private final MemberAddressRepository addressRepository;
    private final SmsService smsService;
    private final BCryptPasswordEncoder encoder; // 주입받기
    private final JwtProvider jwtProvider; // JWT 생성기 주입

    //로그인
    @Override
    public String login(LoginRequestDTO dto){

        // 1. 아이디로만 회원을 조회 (Optional 처리)
        Member member = memberRepository.findByMemberId(dto.getMemberId())
                .orElseThrow(() -> new BusinessException(ErrorCode.MEMBER_NOT_FOUND));

        // 2. 회원이 존재하고 + 암호화된 비밀번호가 일치하는지 체크
        // encoder.matches(평문 비밀번호, DB에 저장된 암호화 비밀번호)
        if (!encoder.matches(dto.getMemberPwd(), member.getMemberPwd())) {
            throw new BusinessException(ErrorCode.INVALID_PASSWORD);
        }

        // 3. DTO 대신 JWT 토큰을 생성해서 반환합니다.
        return jwtProvider.createToken(member.getMemberId());
    }

    //아이디 중복체크
    @Override
    @Transactional(readOnly = true)
    public boolean checkIdDuplicate(String memberId) {
        // 리포지토리에게 DB 조회를 시킴
        return memberRepository.countByMemberId(memberId) > 0;
    }

    //닉네임 중복체크
    @Override
    public boolean checkNicknameDuplicate(String nickname) {
        // 0보다 크면 중복(true), 0이면 사용가능(false)
        return memberRepository.countByNickname(nickname) > 0;
    }

    //핸드폰인증
    @Override
    public String sendPhoneAuthCode(String phone) {
        smsService.sendAuthCode(phone);
        return "success";
    }

    //회원가입
    @Override
    @Transactional
    public boolean joinMember(MemberDTO.JoinRequest joinDto) {
        //if (!smsService.checkIsVerified(joinDto.getPhone())) throw new RuntimeException("인증되지 않은 번호입니다.");
        // 비밀번호 암호화!!
        String rawPassword = joinDto.getMemberPwd();
        String encodedPassword = encoder.encode(rawPassword);
        try {
            // 1. 회원 객체 생성 (MEMBER_NO는 시퀀스로 자동 할당됨)
            // 정적 팩토리 메서드로 생성 책임을 Member 스스로 갖도록 위임
            Member member = Member.create(joinDto, encodedPassword);

            // 2. 먼저 member를 저장해서 PK(memberNo)를 생성함
            Member savedMember = memberRepository.save(member);

            // 3. 저장된 member 정보를 가지고 주소 엔티티 생성
            MemberAddress address = MemberAddress.builder()
                    .member(savedMember) // 생성된 PK 연결
                    .recipientName(joinDto.getMemberName())
                    .recipientPhone(joinDto.getPhone())
                    .zipcode(joinDto.getZipcode())
                    .address(joinDto.getAddress())
                    .addressDetail(joinDto.getAddressDetail())
                    .addrName("기본배송지")
                    .isDefault("Y")
                    .build();

            // 4. 주소 저장
            addressRepository.save(address);

            return true;
        } catch (Exception e) {
            return false;
        }
    }
}
