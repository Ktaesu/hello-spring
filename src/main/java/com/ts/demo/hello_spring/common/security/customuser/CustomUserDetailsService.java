package com.ts.demo.hello_spring.common.security.customuser;

import com.ts.demo.hello_spring.member.entity.Member;
import com.ts.demo.hello_spring.member.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {

    private final MemberRepository memberRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        // DB에서 사용자 조회 (태수님의 Member 엔티티 기준)
        Member member = memberRepository.findByMemberId(username)
                .orElseThrow(() -> new UsernameNotFoundException("해당 아이디를 찾을 수 없습니다: " + username));


        // DB의 ROLE을 Spring Security 권한으로 변환
        SimpleGrantedAuthority authority =
                new SimpleGrantedAuthority("ROLE_" + member.getRole());

        // 시큐리티가 이해할 수 있는 User 객체로 변환해서 반환
        // [수정 포인트] 기본 User가 아닌 CustomUserDetails를 반환!
        return new CustomUserDetails(
                member.getMemberId(),
                member.getMemberPwd(),
                List.of(authority),
                member.getNickname() // DB에서 가져온 닉네임 쏙 넣어주기
        );
    }
}
