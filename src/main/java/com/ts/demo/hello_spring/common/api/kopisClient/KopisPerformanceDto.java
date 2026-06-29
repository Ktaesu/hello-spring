package com.ts.demo.hello_spring.common.api.kopisClient;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import lombok.Data;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class KopisPerformanceDto {
    @JacksonXmlProperty(localName = "mt20id") private String id;     // 공연 ID
    @JacksonXmlProperty(localName = "prfnm") private String title;   // 공연명
    @JacksonXmlProperty(localName = "fcltynm") private String hallName; // 공연장명
    @JacksonXmlProperty(localName = "prfpdfrom") private String startDate;
    @JacksonXmlProperty(localName = "prfpdto") private String endDate;
    @JacksonXmlProperty(localName = "poster") private String posterPath;
    @JacksonXmlProperty(localName = "area")  private String area;
    @JacksonXmlProperty(localName = "prfstate") private String prfstate; // 공연 상태 (공연중, 공연예정 등)
    @JacksonXmlProperty(localName = "genrenm") private String genre;
}
