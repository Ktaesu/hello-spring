package com.ts.demo.hello_spring.product.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor
@Table(name = "hall")
@SequenceGenerator(
        name = "HALL_SEQ_GEN",
        sequenceName = "seq_hall", // 오라클에 생성한 시퀀스명
        allocationSize = 1
)
public class Hall {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "HALL_SEQ_GEN")
    private Long id;

    @Column(nullable = false, length = 100)
    private String name; // 공연장 이름 (우리가 화면에 띄울 값!)

    @Column(length = 300)
    private String address; // 상세 주소

    @Column(name = "total_seats")
    private int totalSeats; // 총 좌석 수 (기본값 0)

    // 만약 양방향 매핑이 필요하다면 여기에 추가하겠지만,
    // 현재 조인 쿼리 방식(PerfSchedule -> Hall)에서는 이 정도로도 충분합니다.
}
