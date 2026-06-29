package com.ts.demo.hello_spring.product.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Entity
@Getter
@NoArgsConstructor
@Table(name = "performance")
@SequenceGenerator(
        name = "PERF_SEQ_GEN",
        sequenceName = "seq_performance", // DB에 만든 시퀀스명
        allocationSize = 1
)
public class Performance {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "PERF_SEQ_GEN")
    private Long id;

    private String title;
    private String category;
    private String isSeated;
    private int price;
    private String imgPath;

    // 날짜 필드가 없을 때 임시 방편
    public String getFormattedPeriod() {
        return "2026.04.19 ~ 2026.05.19"; // 일단 하드코딩해서 에러부터 잡기!
    }
}
