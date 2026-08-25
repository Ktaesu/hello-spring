package com.ts.demo.hello_spring.member.entity;

import com.ts.demo.hello_spring.member.dto.MemberDTO;
import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name="MEMBER")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Member {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "MEMBER_SEQ_GEN")
    @SequenceGenerator(
            name = "MEMBER_SEQ_GEN",        // @GeneratedValue의 generator와 일치해야 함
            sequenceName = "SEQ_MEMBER",    // 실제 Oracle DB에 만든 시퀀스 이름
            initialValue = 11,              // 더미가 10개이므로 11부터
            allocationSize = 1
    )
    private long memberNo;

    @Column(name = "MEMBER_ID")
    private String memberId;

    @Column(name = "MEMBER_PWD")
    private String memberPwd;

    @Column(name = "MEMBER_NAME")
    private String memberName;

    @Column(name = "NICKNAME")
    private  String nickname;

    @Column(name = "PHONE")
    private String phone;

    @Column(name = "EMAIL")
    private String email;

    @Column(name = "ROLE")
    private String role;

    // 회원이 저장될 때 주소도 함께 저장되도록 Cascade 설정
    @OneToMany(mappedBy = "member", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<MemberAddress> addresses = new ArrayList<>();

    /**
     * 회원 생성을 위한 정적 팩토리 메서드.
     * 가입 시 필요한 필드만 받아서 객체를 만들고,
     * 생성 이후 필드 값 변경(setter)이 불가능하도록 캡슐화한다.
     */
    public static Member create(MemberDTO.JoinRequest dto, String encodedPassword) {
        Member member = new Member();
        member.memberId = dto.getMemberId();
        member.memberPwd = encodedPassword;
        member.memberName = dto.getMemberName();
        member.nickname = dto.getNickname();
        member.phone = dto.getPhone();
        member.email = dto.getEmail();
        return member;
    }

}
