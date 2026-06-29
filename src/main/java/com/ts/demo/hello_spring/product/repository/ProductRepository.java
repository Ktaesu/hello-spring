package com.ts.demo.hello_spring.product.repository;

import com.ts.demo.hello_spring.product.dto.PerformanceListDto;
import com.ts.demo.hello_spring.product.entity.Performance;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface ProductRepository extends JpaRepository<Performance, Long> {
    @Query("SELECT new com.ts.demo.hello_spring.product.dto.PerformanceListDto(p, s.startAt, s.endAt, h.name) " +
            "FROM PerfSchedule s " +
            "JOIN s.performance p " +
            "JOIN s.hall h " +
            "WHERE p.category = :category")
    List<PerformanceListDto> findAllWithScheduleByCategory(@Param("category") String category);
}
