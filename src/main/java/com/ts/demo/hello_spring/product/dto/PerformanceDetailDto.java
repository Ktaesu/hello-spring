package com.ts.demo.hello_spring.product.dto;

import com.ts.demo.hello_spring.common.api.kopisClient.KopisDetailDto;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.List;

@Getter
@Slf4j
public class PerformanceDetailDto {

    private String mt20id;
    private String prfnm;
    private String prfpdfrom;
    private String prfpdto;
    private String fcltynm;
    private String prfruntime;
    private String prfage;
    private String pcseguidance;
    private String poster;
    private String area;
    private String genrenm;
    private String prfstate;
    private String sty;
    private String adres;
    private double lat;
    private double lng;
    private String minPrice;
    private List<String> styurls = new ArrayList<>();

    // 가격 파싱용 내부 클래스
    @Getter
    public static class PriceItem {
        private final String seatGrade;
        private final String price;

        public PriceItem(String seatGrade, String price) {
            this.seatGrade = seatGrade;
            this.price = price;
        }
    }

    private List<PriceItem> priceList = new ArrayList<>();

    // 출연진 파싱용 내부 클래스
    @Getter
    public static class CastItem {
        private final String role;
        private final String name;
        private final String imgUrl;

        public CastItem(String role, String name, String imgUrl) {
            this.role   = role;
            this.name   = name;
            this.imgUrl = imgUrl;
        }
    }

    private List<CastItem> castList = new ArrayList<>();

    // ── KopisDetailDto → PerformanceDetailDto 변환 ──
    public static PerformanceDetailDto from(KopisDetailDto dto) {
        PerformanceDetailDto d = new PerformanceDetailDto();

        d.mt20id       = dto.getMt20id();
        d.prfnm        = dto.getPrfnm();
        d.prfpdfrom    = dto.getPrfpdfrom();
        d.prfpdto      = dto.getPrfpdto();
        d.fcltynm      = dto.getFcltynm();
        d.prfruntime   = dto.getPrfruntime();
        d.prfage       = dto.getPrfage();
        d.pcseguidance = dto.getPcseguidance();
        d.poster       = dto.getPoster();
        d.area         = dto.getArea();
        d.genrenm      = dto.getGenrenm();
        d.prfstate     = dto.getPrfstate();
        d.sty          = dto.getSty();
        d.styurls = dto.getStyurls() != null ? dto.getStyurls() : new ArrayList<>();
        log.info("소개 이미지 개수={}, urls={}",
                dto.getStyurls() != null ? dto.getStyurls().size() : 0,
                dto.getStyurls());

        // 가격 파싱 — "VIP석 170,000원, R석 130,000원" 형태
        d.priceList = parsePrices(dto.getPcseguidance());
        d.minPrice  = d.priceList.isEmpty() ? "가격 문의"
                : d.priceList.get(d.priceList.size() - 1).getPrice(); // 마지막(최저가)

        // 출연진 파싱 — "홍길동, 김철수, ..." 형태
        d.castList = parseCast(dto.getPrfcast());

        return d;
    }

    // "VIP석 170,000원, R석 130,000원" → PriceItem 리스트
    private static List<PriceItem> parsePrices(String raw) {
        List<PriceItem> list = new ArrayList<>();
        if (raw == null || raw.isBlank()) return list;

        String[] parts = raw.split(", ");
        for (String part : parts) {
            String p = part.trim();
            // 마지막 공백 기준으로 등급 / 가격 분리
            int lastSpace = p.lastIndexOf(' ');
            if (lastSpace > 0) {
                list.add(new PriceItem(
                        p.substring(0, lastSpace).trim(),
                        p.substring(lastSpace + 1).trim()
                ));
            } else {
                list.add(new PriceItem("일반", p));
            }
        }
        return list;
    }

    // "홍길동, 김철수" → CastItem 리스트
    private static List<CastItem> parseCast(String raw) {
        List<CastItem> list = new ArrayList<>();
        if (raw == null || raw.isBlank()) return list;

        String[] names = raw.split(",");
        for (String name : names) {
            String n = name.trim();
            if (!n.isEmpty()) {
                list.add(new CastItem("출연", n, null));
            }
        }
        return list;
    }

    // 공연시설 정보 적용
    public void applyFacility(String adres, String lat, String lng) {
        this.adres = adres;
        try { this.lat = Double.parseDouble(lat); } catch (Exception e) { this.lat = 37.5665; }
        try { this.lng = Double.parseDouble(lng); } catch (Exception e) { this.lng = 126.9780; }
    }
}