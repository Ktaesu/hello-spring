package com.ts.demo.hello_spring.member.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name="MEMBER")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
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

    // 회원이 저장될 때 주소도 함께 저장되도록 Cascade 설정
    @OneToMany(mappedBy = "member", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<MemberAddress> addresses = new ArrayList<>();

}
