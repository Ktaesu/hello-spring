package com.ts.demo.hello_spring.common.api.kopisClient;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(name = "kopisClient", url = "http://www.kopis.or.kr/openApi/restful")
public interface KopisClient {

    @GetMapping("/pblprfr") // 공연 목록 조회 엔드포인트
    String getPerformanceList(
            @RequestParam("service") String apiKey,
            @RequestParam("stdate") String startDate,
            @RequestParam("eddate") String endDate,
            @RequestParam("cpage") int cpage,
            @RequestParam("rows") int rows,
            @RequestParam("shcate") String category, // 예: AAAA(연극), BBBB(무용) 등
            @RequestParam("prfstate") String prfstate,    // 추가: 공연 상태 (02: 공연중)
            @RequestParam("signgucode") String areaCode  // 추가: 지역 코드 (11, 41 등)
    );

    // KopisClient.java
    @GetMapping("/boxoffice")
    String getBoxOffice(
            @RequestParam("service") String apiKey,
            @RequestParam("stdate") String stdate,   // 시작 날짜
            @RequestParam("eddate") String eddate,      // 종료일 (보통 오늘과 같게 설정)
            @RequestParam("catecode") String catecode,  // 장르 코드
            @RequestParam("area") String area           // 지역 코드 (서울: 11 등)
    );

    @GetMapping("/pblprfr")
    String searchPerformanceByName(
            @RequestParam("service") String apiKey,
            @RequestParam("shprfnm") String keyword,   // 공연명 검색어
            @RequestParam("stdate") String stdate,
            @RequestParam("eddate") String eddate,
            @RequestParam("cpage") int cpage,
            @RequestParam("rows") int rows
    );

    // 기존 메서드들 아래에 추가
    @GetMapping("/pblprfr/{mt20id}")
    String getPerformanceDetail(
            @RequestParam("service") String apiKey,
            @PathVariable("mt20id") String mt20id
    );

    @GetMapping("/prfplc/{mt10id}")
    String getFacilityDetail(
            @RequestParam("service") String apiKey,
            @PathVariable("mt10id") String mt10id
    );

}
