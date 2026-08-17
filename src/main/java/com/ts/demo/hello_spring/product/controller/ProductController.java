package com.ts.demo.hello_spring.product.controller;

import com.ts.demo.hello_spring.common.api.ApiResponse;
import com.ts.demo.hello_spring.product.dto.PerformanceDetailDto;
import com.ts.demo.hello_spring.product.dto.PerformanceListDto;
import com.ts.demo.hello_spring.product.service.ProductService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RequestMapping("/product")
@RequiredArgsConstructor
@Controller
public class ProductController {

    private final ProductService productService;

    @Value("${kakao.map.key:}")
    private String kakaoMapKey;

    @GetMapping("/mainProduct")
    public String mainProduct(){

        return "product/productMain";
    }

    @GetMapping("/artTicket")
    public String artTicketList(Model model){
        List<PerformanceListDto> artList = productService.getFilteredArtList(null, "daily");
        model.addAttribute("performances", artList);

        return"product/artTicket";
    }

    // AJAX 필터링 요청 (URL 변경 없이 리스트만 교체)
    @GetMapping("/api/filter-art")
    public String filterArtList(
            @RequestParam(value = "area", required = false) String area,
            @RequestParam(value = "rank", defaultValue = "daily") String rank,
            Model model) {

        List<PerformanceListDto> filteredList = productService.getFilteredArtList(area, rank);
        model.addAttribute("performances", filteredList);

        // Thymeleaf Fragment 반환: artTicket.html의 id="performanceList" 부분만 잘라서 보냄
        return "product/artTicket :: #performanceList";
    }

    @GetMapping("/api/search")
    @ResponseBody
    public ApiResponse<List<PerformanceListDto>> searchPerformances(
            @RequestParam String keyword) {

        if (keyword == null || keyword.trim().length() < 2) {
            return ApiResponse.fail("검색어는 2글자 이상 입력해주세요.");
        }

        List<PerformanceListDto> result = productService.searchPerformances(keyword.trim());
        return ApiResponse.success(result);
    }

    @GetMapping("/detail/{mt20id}")
    public String performanceDetail(
            @PathVariable String mt20id, Model model) {

        PerformanceDetailDto performance = productService.getPerformanceDetail(mt20id);
        log.info("주소(adres): {}", performance.getAdres());
        model.addAttribute("performance", performance);
        model.addAttribute("kakaoMapKey", kakaoMapKey); // ✅ application.yml에서 주입
        model.addAttribute("reviews", List.of());       // 추후 DB 연동

        return "product/detailTicket";
    }
}
