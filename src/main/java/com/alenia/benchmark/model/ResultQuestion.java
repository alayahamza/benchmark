package com.alenia.benchmark.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ResultQuestion {
    private String value;
    private Integer startIndex;
    private Integer endIndex;
    private List<Response> responses;
    private String section;
    private String subSection;
}
