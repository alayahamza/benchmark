package com.alenia.benchmark.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.IntSummaryStatistics;
import java.util.List;
import java.util.Map;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class FinalResult {
    private Map<String, List<Subject>> individualStatistics;
    private Map<String, IntSummaryStatistics> subSectionStatistics;
    private Map<String, IntSummaryStatistics> sectionStatistics;
}
