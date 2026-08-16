package com.ts.demo.hello_spring.product.dto;

import com.ts.demo.hello_spring.product.entity.Performance;
import lombok.Getter;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Getter
public class PerformanceListDto {
    private Long id;
    private String title;
    private String imgPath;
    private String location;
    private String formattedPeriod; // 가공된 날짜 문자열
    private String endDate;
    private int price;
    private String area;    // ✅ 추가
    private String genre;
    private String mt20id;

    // ── 핵심 생성자 ──
    private PerformanceListDto(String mt20id, String title, String imgPath, String location,
                               String formattedPeriod, String endDate, String area, String genre) {
        this.mt20id = mt20id;
        this.title = title;
        this.imgPath = imgPath;
        this.location = location;
        this.formattedPeriod = formattedPeriod;
        this.endDate = endDate;
        this.area = area;
        this.genre = genre;
    }

    // ── DB 엔티티용 ──
    public PerformanceListDto(Performance p, LocalDateTime start, LocalDateTime end, String hallName) {
        this.id = p.getId();
        this.title = p.getTitle();
        this.imgPath = p.getImgPath();
        this.price = p.getPrice();
        this.location = hallName;
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy.MM.dd");
        this.formattedPeriod = (start != null ? start.format(dtf) : "") + " ~ " +
                (end != null ? end.format(dtf) : "");
    }

    // ── 정적 팩토리 메서드 ──

    // 일반 목록 / 종료임박용
    public static PerformanceListDto ofGeneral(String mt20id, String title, String imgPath,
                                               String location, String start, String end) {
        return new PerformanceListDto(
                mt20id, title, imgPath, location,
                start + " ~ " + end, end, null, null
        );
    }

    // 랭킹(BoxOffice)용
    public static PerformanceListDto ofRanking(String mt20id, String title, String imgPath,
                                               String location, String period) {
        String endDate = null;
        if (period != null && period.contains("~")) {
            endDate = period.split("~")[1].trim().replace(".", "");
        }
        return new PerformanceListDto(
                mt20id, title, imgPath, location,
                period, endDate, null, null
        );
    }

    // 검색용
    public static PerformanceListDto ofSearch(String mt20id, String title, String imgPath,
                                              String location, String start, String end,
                                              String area, String genre) {
        return new PerformanceListDto(
                mt20id, title, imgPath, location,
                start + " ~ " + end, end, area, genre
        );
    }
}
