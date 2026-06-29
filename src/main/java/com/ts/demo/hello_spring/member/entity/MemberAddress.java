package com.ts.demo.hello_spring.member.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class MemberAddress {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "ADDR_SEQ_GEN")
    @SequenceGenerator(
            name = "ADDR_SEQ_GEN",
            sequenceName = "SEQ_MEMBER_ADDRESS", // 방금 만든 시퀀스 이름
            allocationSize = 1
    )
    private Long addrNo;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_no")
    private Member member;

    private String recipientName;
    private String recipientPhone;
    private String zipcode;
    private String address;
    private String addressDetail;
    private String addrName;
    private String isDefault;
}
