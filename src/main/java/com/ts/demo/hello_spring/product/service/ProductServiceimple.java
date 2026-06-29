package com.ts.demo.hello_spring.product.service;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import com.ts.demo.hello_spring.common.api.kopisClient.KopisBoxOfficeResponseDto;
import com.ts.demo.hello_spring.common.api.kopisClient.KopisClient;
import com.ts.demo.hello_spring.common.api.kopisClient.KopisDetailResponseDto;
import com.ts.demo.hello_spring.common.api.kopisClient.KopisResponseDto;
import com.ts.demo.hello_spring.product.dto.PerformanceDetailDto;
import com.ts.demo.hello_spring.product.dto.PerformanceListDto;
import com.ts.demo.hello_spring.product.repository.ProductRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;
import java.util.Set;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ProductServiceimple implements ProductService {

    private final KopisClient kopisClient;
    private final XmlMapper xmlMapper = new XmlMapper(){{
        configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
    }};

    @Value("${kopis.api.key}")
    private String API_KEY;

    private final ProductRepository productRepository;

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
        boolean isRanking = Set.of("daily", "weekly", "monthly").contains(rankType);

        List<PerformanceListDto> rankingList = isRanking
                ? getBoxOfficeRanking(apiAreaCode, rankType)
                : Collections.emptyList();

        List<PerformanceListDto> generalList = getGeneralPerformanceList(apiAreaCode, rankType);

        // 중복 제거 후 통합
        Set<String> rankingTitles = rankingList.stream()
                .map(PerformanceListDto::getTitle)
                .collect(Collectors.toSet());

        List<PerformanceListDto> combined = new ArrayList<>(rankingList);
        generalList.stream()
                .filter(item -> !rankingTitles.contains(item.getTitle()))
                .forEach(combined::add);

        return isRanking ? combined : sortList(combined, rankType);
    }

    @Override
    public List<PerformanceListDto> searchPerformances(String keyword) {
        LocalDate now = LocalDate.now();
        String stdate = now.minusMonths(3).format(DATE_FMT);
        String eddate = now.plusMonths(12).format(DATE_FMT);

        try {
            String xml = kopisClient.searchPerformanceByName(API_KEY, keyword, stdate, eddate, 1, 10);
            KopisResponseDto response = parseXml(xml, KopisResponseDto.class);

            if (response == null || response.getPerformances() == null) {
                return Collections.emptyList();
            }

            return response.getPerformances().stream()
                    .filter(item -> VALID_STATES.contains(item.getPrfstate()))
                    .map(item -> new PerformanceListDto(
                            item.getId(), item.getTitle(), item.getPosterPath(), item.getHallName(),
                            item.getStartDate(), item.getEndDate(),
                            item.getArea(), item.getGenre()
                    ))
                    .collect(Collectors.toList());

        } catch (Exception e) {
            return Collections.emptyList();
        }
    }

    @Override
    public PerformanceDetailDto getPerformanceDetail(String mt20id) {
        try {
            String xml = kopisClient.getPerformanceDetail(API_KEY, mt20id);
            KopisDetailResponseDto response = parseXml(xml, KopisDetailResponseDto.class);

            if (response == null || response.getDetails() == null || response.getDetails().isEmpty()) {
                throw new EntityNotFoundException("공연 정보를 찾을 수 없습니다: " + mt20id);
            }

            return PerformanceDetailDto.from(response.getDetails().get(0));

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
            KopisBoxOfficeResponseDto response = parseXml(xml, KopisBoxOfficeResponseDto.class);

            if (response == null || response.getBoxOfficeList() == null) {
                return Collections.emptyList();
            }

            return response.getBoxOfficeList().stream()
                    .map(item -> new PerformanceListDto(
                            item.getPrfnm(), item.getPoster(),
                            item.getPrfplcnm(), item.getPrfpd()
                    ))
                    .collect(Collectors.toList());

        } catch (Exception e) {
            return getGeneralPerformanceList(areaCode, rankType);
        }
    }

    private List<PerformanceListDto> getGeneralPerformanceList(String areaCode, String rankType) {
        LocalDate now = LocalDate.now();
        String stdate = now.minusMonths(3).format(DATE_FMT);
        String eddate = now.plusMonths(6).format(DATE_FMT);

        String[] codes = (areaCode != null && !areaCode.isBlank())
                ? areaCode.split("\\|")
                : new String[]{null};

        List<PerformanceListDto> totalList = new ArrayList<>();

        for (String code : codes) {
            try {
                String xml = kopisClient.getPerformanceList(
                        API_KEY, stdate, eddate, 1, 20, "AAAA", "0102", code);
                KopisResponseDto response = parseXml(xml, KopisResponseDto.class);

                if (response == null || response.getPerformances() == null) continue;

                response.getPerformances().stream()
                        .filter(item -> VALID_STATES.contains(item.getPrfstate()))
                        .map(item -> new PerformanceListDto(
                                item.getTitle(), item.getPosterPath(), item.getHallName(),
                                item.getStartDate(), item.getEndDate()
                        ))
                        .forEach(totalList::add);

            } catch (Exception e) {
            }
        }

        return totalList.isEmpty() ? Collections.emptyList() : sortList(totalList, rankType);
    }

    private List<PerformanceListDto> sortList(List<PerformanceListDto> list, String rankType) {
        if ("closing".equals(rankType)) {
            return list.stream()
                    .sorted(Comparator.comparing(PerformanceListDto::getEndDate))
                    .collect(Collectors.toList());
        }
        return list;
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