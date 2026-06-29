package com.ts.demo.hello_spring.common.api;

import com.ts.demo.hello_spring.product.dto.PerformanceListDto;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;

@Getter
@AllArgsConstructor
public class ApiResponse<T> {
    private boolean success; // "SUCCESS" 또는 "FAIL"
    private String message;  // 사용자에게 보여줄 메시지
    private T data;         // 실제 결과 데이터 (성공 시에만 주로 사용)

    // 성공 응답을 만드는 정적 메서드
    public static <T> ApiResponse<T> success(T data) {
        return new ApiResponse<>(true, "요청 성공", data);
    }

    // 실패 응답을 만드는 정적 메서드
    public static <T> ApiResponse<T> fail(String message) {
        return new ApiResponse<>(false, message, null);
    }

}
