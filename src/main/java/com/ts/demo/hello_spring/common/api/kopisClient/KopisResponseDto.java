package com.ts.demo.hello_spring.common.api.kopisClient;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlElementWrapper;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlRootElement;
import lombok.Data;

import java.util.List;

@Data
@JacksonXmlRootElement(localName = "dbs") // XML의 최상위 태그
public class KopisResponseDto {
    @JacksonXmlProperty(localName = "db")
    @JacksonXmlElementWrapper(useWrapping = false)
    private List<KopisPerformanceDto> performances;


}
