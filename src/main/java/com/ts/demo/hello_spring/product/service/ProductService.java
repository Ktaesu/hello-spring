package com.ts.demo.hello_spring.product.service;

import com.ts.demo.hello_spring.product.dto.PerformanceDetailDto;
import com.ts.demo.hello_spring.product.dto.PerformanceListDto;

import java.util.List;

public interface ProductService {


    List<PerformanceListDto> getFilteredArtList(String area, String rank);

    List<PerformanceListDto> searchPerformances(String keyword);

    PerformanceDetailDto getPerformanceDetail(String mt20id);
}
