package com.ts.demo.hello_spring.common.api.kopisClient;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlElementWrapper;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlRootElement;
import lombok.Data;

import java.util.List;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
@JacksonXmlRootElement(localName = "db")
public class KopisDetailDto {

    @JacksonXmlProperty(localName = "mt20id")   private String mt20id;      // 공연ID
    @JacksonXmlProperty(localName = "prfnm")    private String prfnm;       // 공연명
    @JacksonXmlProperty(localName = "prfpdfrom") private String prfpdfrom;  // 시작일
    @JacksonXmlProperty(localName = "prfpdto")  private String prfpdto;     // 종료일
    @JacksonXmlProperty(localName = "fcltynm")  private String fcltynm;     // 공연시설명
    @JacksonXmlProperty(localName = "prfcast")  private String prfcast;     // 출연진
    @JacksonXmlProperty(localName = "prfcrew")  private String prfcrew;     // 제작진
    @JacksonXmlProperty(localName = "prfruntime") private String prfruntime; // 공연시간
    @JacksonXmlProperty(localName = "prfage")   private String prfage;      // 관람연령
    // ✅ 공연시간/요일 안내 XML 태그 매핑 (<dtguidance>...</dtguidance>)
    @JacksonXmlProperty(localName = "dtguidance") private String dtguidance;
    @JacksonXmlProperty(localName = "pcseguidance") private String pcseguidance; // 가격
    @JacksonXmlProperty(localName = "poster")   private String poster;      // 포스터URL
    @JacksonXmlProperty(localName = "area")     private String area;        // 지역
    @JacksonXmlProperty(localName = "genrenm")  private String genrenm;     // 장르
    @JacksonXmlProperty(localName = "prfstate") private String prfstate;    // 공연상태
    @JacksonXmlProperty(localName = "sty")      private String sty;         // 줄거리
    @JacksonXmlProperty(localName = "lat")      private String lat;         // 위도
    @JacksonXmlProperty(localName = "lot")      private String lng;         // 경도
    @JacksonXmlProperty(localName = "adres")    private String adres;       // 주소
    @JacksonXmlProperty(localName = "mt10id") private String mt10id; // 공연시설 ID
    // 소개 이미지
    @JacksonXmlProperty(localName = "styurls")
    @JacksonXmlElementWrapper(localName = "styurls")
    private List<String> styurls;
}