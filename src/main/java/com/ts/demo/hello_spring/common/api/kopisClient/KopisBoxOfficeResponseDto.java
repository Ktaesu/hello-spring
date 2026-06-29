package com.ts.demo.hello_spring.common.api.kopisClient;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlElementWrapper;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlRootElement;
import lombok.Data;

import java.util.List;

@Data
@JacksonXmlRootElement(localName = "boxofs")
@JsonIgnoreProperties(ignoreUnknown = true)
public class KopisBoxOfficeResponseDto {


    @JacksonXmlProperty(localName = "boxof")
    @JacksonXmlElementWrapper(useWrapping = false)
    private List<BoxOfficeItem> boxOfficeList;

    @Data
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class BoxOfficeItem {
        @JacksonXmlProperty(localName = "prfnm")
        private String prfnm;    // 공연명
        @JacksonXmlProperty(localName = "prfpd")
        private String prfpd;    // 공연기간
        @JacksonXmlProperty(localName = "prfplcnm")
        private String prfplcnm; // 공연장소
        @JacksonXmlProperty(localName = "poster")
        private String poster;   // 포스터경로
        @JacksonXmlProperty(localName = "mt20id")
        private String mt20id;   // 공연ID
        @JacksonXmlProperty(localName = "rnum")
        private String rnum;     // 순위
        @JacksonXmlProperty(localName = "cate")
        private String cate;     // 장르 (전시/행사는 보통 '미술'이나 '기타'로 분류)
    }
}
