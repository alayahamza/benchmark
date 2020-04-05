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
public class NotationQuestion {
    private String value;
    private QuestionType questionType;
    private List<Response> responses;
    private Integer note;
    private String section;
    private String subSection;
}
