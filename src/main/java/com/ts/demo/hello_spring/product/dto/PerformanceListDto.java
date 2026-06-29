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

    // 네 번째 인자를 주소(address) 대신 이름(hallName)으로 받습니다.
    public PerformanceListDto(Performance p, LocalDateTime start, LocalDateTime end, String hallName) {
        this.id = p.getId();
        this.title = p.getTitle();
        this.imgPath = p.getImgPath();
        this.price = p.getPrice();
        this.location = hallName; // 공연장 이름을 location 필드에 매핑

        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy.MM.dd");
        this.formattedPeriod = (start != null ? start.format(dtf) : "") + " ~ " +
                (end != null ? end.format(dtf) : "");
    }

    // PerformanceListDto.java에 추가
    public PerformanceListDto(String title, String imgPath, String location, String start, String end) {
        this.title = title;
        this.imgPath = imgPath;
        this.location = location;
        this.endDate = end;
        this.formattedPeriod = start + " ~ " + end;
    }

    // [추가] 랭킹(BoxOffice) 전용 (파라미터 4개)
    public PerformanceListDto(String title, String imgPath, String location, String period) {
        this.title = title;
        this.imgPath = imgPath;
        this.location = location;
        this.formattedPeriod = period; // "2026.04.01~2026.05.01" 그대로 저장

        // 정렬용 endDate 추출 (선택 사항: 랭킹은 이미 정렬되어 오므로 비워둬도 됨)
        if (period != null && period.contains("~")) {
            this.endDate = period.split("~")[1].trim().replace(".", "");
        }
    }

    // ✅ 검색용 생성자 (area, genre 포함)
    public PerformanceListDto(String mt20id, String title, String imgPath, String location,
                              String start, String end, String area, String genre) {
        this.mt20id = mt20id;
        this.title = title;
        this.imgPath = imgPath;
        this.location = location;
        this.endDate = end;
        this.formattedPeriod = start + " ~ " + end;
        this.area = area;
        this.genre = genre;
    }

}
