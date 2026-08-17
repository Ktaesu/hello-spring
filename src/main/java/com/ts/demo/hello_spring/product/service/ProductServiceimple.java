package com.ts.demo.hello_spring.product.service;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import com.ts.demo.hello_spring.common.api.kopisClient.*;
import com.ts.demo.hello_spring.product.dto.PerformanceDetailDto;
import com.ts.demo.hello_spring.product.dto.PerformanceListDto;
import com.ts.demo.hello_spring.product.repository.ProductRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;


@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ProductServiceimple implements ProductService {

    private final KopisClient kopisClient;
    private final ProductRepository productRepository;

    private final XmlMapper xmlMapper = new XmlMapper(){{
        configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
    }};

    @Value("${kopis.api.key}")
    private String API_KEY;

    // ✅ 날짜 포맷 상수화
    private static final DateTimeFormatter DATE_FMT = DateTimeFormatter.ofPattern("yyyyMMdd");

    // ✅ 공연 상태 필터 공통화
    private static final Set<String> VALID_STATES = Set.of("공연중", "공연예정", "01", "02");

    // ============================================================
    // Public API
    // ============================================================

    @Override
    public List<PerformanceListDto> getFilteredArtList(String areaCode, String rankType) {
        String apiAreaCode = blankToNull(areaCode);
        log.info("[ProductService] getFilteredArtList 호출 - areaCode: {}, rankType: {}", apiAreaCode, rankType);

        List<PerformanceListDto> result =  switch (rankType) {
            case "daily", "weekly", "monthly" -> getBoxOfficeRanking(apiAreaCode, rankType);
            case "closing"                    -> getClosingPerformanceList(apiAreaCode);
            default                           -> getBoxOfficeRanking(apiAreaCode, "daily");
        };

        log.info("[ProductService] 필터링 최종 조회 결과 건수: {}건", result.size());
        return result;
    }

    @Override
    public List<PerformanceListDto> searchPerformances(String keyword) {
        LocalDate now = LocalDate.now();
        String stdate = now.format(DATE_FMT);
        String eddate = now.plusMonths(12).format(DATE_FMT);

        try {
            String xml = kopisClient.searchPerformanceByName(API_KEY, keyword, stdate, eddate, 1, 10);
            KopisResponseDto response = parseXml(xml, KopisResponseDto.class);

            if (response == null || response.getPerformances() == null) {
                return Collections.emptyList();
            }

            return response.getPerformances().stream()
                    .map(item -> PerformanceListDto.ofSearch(
                            item.getId(), item.getTitle(), item.getPosterPath(),
                            item.getHallName(), item.getStartDate(), item.getEndDate(),
                            item.getArea(), item.getGenre()
                    ))
                    .collect(Collectors.toList());

        } catch (Exception e) {
            log.error("KOPIS 검색 실패: keyword={}, error={}", keyword, e.getMessage());
            return Collections.emptyList();
        }
    }

    // 공연 상세 — 1시간 캐싱
    @Cacheable(value = "performanceDetail", key = "#mt20id")
    @Override
    public PerformanceDetailDto getPerformanceDetail(String mt20id) {
        try {
            String xml = kopisClient.getPerformanceDetail(API_KEY, mt20id);
            KopisDetailResponseDto response = parseXml(xml, KopisDetailResponseDto.class);

            if (response == null || response.getDetails() == null || response.getDetails().isEmpty()) {
                throw new EntityNotFoundException("공연 정보를 찾을 수 없습니다: " + mt20id);
            }

            KopisDetailDto detailDto = response.getDetails().get(0);
            PerformanceDetailDto result = PerformanceDetailDto.from(detailDto);

            try {
                String facilityXml = kopisClient.getFacilityDetail(API_KEY, detailDto.getMt10id());
                KopisDetailResponseDto facilityResponse = parseXml(facilityXml, KopisDetailResponseDto.class);

                if (facilityResponse != null && facilityResponse.getDetails() != null
                        && !facilityResponse.getDetails().isEmpty()) {
                    KopisDetailDto facility = facilityResponse.getDetails().get(0);
                    result.applyFacility(facility.getAdres(), facility.getLat(), facility.getLng());
                }
            } catch (Exception e) {
                log.warn("공연시설 정보 조회 실패: mt10id={}", detailDto.getMt10id());
            }

            return result;

        } catch (EntityNotFoundException e) {
            throw e;
        } catch (Exception e) {
            log.error("공연 상세 조회 실패: mt20id={}, error={}", mt20id, e.getMessage());
            throw new RuntimeException("공연 정보 조회 중 오류가 발생했습니다.");
        }
    }

    // ============================================================
    // Private — 내부 로직
    // ============================================================

    // 박스오피스 — 1시간 캐싱
    private List<PerformanceListDto> getBoxOfficeRanking(String areaCode, String rankType) {
        LocalDate now = LocalDate.now();
        String eddate = now.minusDays(1).format(DATE_FMT);
        String stdate = switch (rankType) {
            case "monthly" -> now.minusMonths(1).format(DATE_FMT);
            case "weekly"  -> now.minusDays(7).format(DATE_FMT);
            default        -> eddate; // daily
        };

        try {
            String xml = kopisClient.getBoxOffice(API_KEY, stdate, eddate, "AAAA", areaCode);

            // ✅ KOPIS가 보낸 실제 XML 데이터 확인용 로그 추가
            log.info("=== KOPIS 박스오피스 응답 원본 XML ===\n{}", xml);

            KopisBoxOfficeResponseDto response = parseXml(xml, KopisBoxOfficeResponseDto.class);

            if (response == null || response.getBoxOfficeList() == null) {
                return Collections.emptyList();
            }

            return response.getBoxOfficeList().stream()
                    .map(item -> PerformanceListDto.ofRanking(
                            item.getMt20id(), item.getPrfnm(), item.getPoster(),
                            item.getPrfplcnm(), item.getPrfpd()
                    ))
                    .collect(Collectors.toList());

        } catch (Exception e) {
            log.error("박스오피스 조회 실패: {}", e.getMessage());
            return Collections.emptyList();

        }
    }

    private List<PerformanceListDto> getClosingPerformanceList(String areaCode) {
        LocalDate now = LocalDate.now();
        String stdate = now.format(DATE_FMT);
        String eddate = now.plusDays(14).format(DATE_FMT); // 2주 내 종료 공연

        String[] codes = (areaCode != null && !areaCode.isBlank())
                ? areaCode.split("\\|")
                : new String[]{null};

        List<PerformanceListDto> totalList = new ArrayList<>();

        for (String code : codes) {
            try {
                String xml = kopisClient.getPerformanceList(
                        API_KEY, stdate, eddate, 1, 20, "AAAA", "02", code);
                KopisResponseDto response = parseXml(xml, KopisResponseDto.class);

                if (response == null || response.getPerformances() == null) continue;

                response.getPerformances().stream()
                        .map(item -> PerformanceListDto.ofGeneral(
                                item.getId(), item.getTitle(), item.getPosterPath(),
                                item.getHallName(), item.getStartDate(), item.getEndDate()
                        ))
                        .forEach(totalList::add);

            } catch (Exception e) {
                log.warn("종료임박 조회 실패 [{}]: {}", code, e.getMessage());
            }
        }

        // 종료일 빠른 순 정렬
        return totalList.stream()
                .sorted(Comparator.comparing(PerformanceListDto::getEndDate))
                .collect(Collectors.toList());
    }


    // ✅ XML 파싱 공통 메서드
    private <T> T parseXml(String xml, Class<T> clazz) throws Exception {
        return xmlMapper.readValue(xml, clazz);
    }

    // ✅ null/빈 문자열 공통 처리
    private String blankToNull(String value) {
        return (value == null || value.isBlank()) ? null : value;
    }
}