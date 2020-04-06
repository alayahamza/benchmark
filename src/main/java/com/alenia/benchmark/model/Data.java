package com.alenia.benchmark.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;

import java.util.IntSummaryStatistics;
import java.util.List;
import java.util.Map;

@lombok.Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Data {
    private Map<String, List<Subject>> individualStatistics;
    private List<Company> subSectionStatistics;
    private List<Company> sectionStatistics;
}
