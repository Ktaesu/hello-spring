package com.ts.demo.hello_spring.member.repository;

import com.ts.demo.hello_spring.member.entity.Member;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface MemberRepository extends JpaRepository<Member, Long> {

    //로그인
    Optional<Member> findByMemberId(String memberId);


    //아이디 중복체크
    long countByMemberId(String memberId);

    //닉네임 중복체크
    long countByNickname(String nickname);
}
