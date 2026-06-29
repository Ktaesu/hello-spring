package com.ts.demo.hello_spring.common.api.kopisClient;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlElementWrapper;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlRootElement;
import lombok.Data;

import java.util.List;

@Data
@JacksonXmlRootElement(localName = "dbs")
public class KopisDetailResponseDto {

    @JacksonXmlProperty(localName = "db")
    @JacksonXmlElementWrapper(useWrapping = false)
    private List<KopisDetailDto> details;
}