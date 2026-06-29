package com.ts.demo.hello_spring.product.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Getter
@NoArgsConstructor
@Table(name = "perf_schedule")
public class PerfSchedule {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "SCHED_SEQ_GEN")
    private Long id;

    // 핵심: Performance 엔티티와 연결 (DB의 performance_id 컬럼과 매핑)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "performance_id")
    private Performance performance;

    @Column(name = "start_at")
    private LocalDateTime startAt; // 시작 시간

    // 만약 종료 시간도 스케줄에 있다면 추가 (없으면 DB에도 추가 필요)
    @Column(name = "end_at")
    private LocalDateTime endAt;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "hall_id") // DB의 hall_id 컬럼과 연결
    private Hall hall; // Hall 엔티티가 필요합니다!
}
