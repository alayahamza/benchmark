package com.alenia.benchmark.service;

import com.alenia.benchmark.model.*;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.text.DecimalFormat;
import java.util.*;
import java.util.stream.Collectors;

@Service
@Slf4j
public class BenchmarkService {

    private static final Integer RESPONSE_SHEET_POSITION = 1;
    private static final Integer METADATA_ROW_POSITION = 0;
    private static final Integer SECTION_CELL_INDEX = 0;
    private static final Integer SUBSECTION_CELL_INDEX = 1;
    private static final Integer QUESTION_TYPE_CELL_INDEX = 2;
    private static final Integer NOTE_CELL_INDEX = 3;
    private static final Integer QUESTION_CELL_INDEX = 4;
    private static final Integer RESPONSE_CELL_INDEX = 4;

    private static final Integer NOTATION_STYLESHEET_POSITION = 0;
    private static final Integer QUESTION_START_POSITION = 8;
    private static final Integer RESPONSES_POSITION = 1;
    private static final Integer SUBJECT_RESPONDENT_ID_CELL_INDEX = 0;

    public List<Subject> analyseResponses(File file, List<NotationQuestion> notationQuestions) {
        List<Subject> subjects = new ArrayList<>();
        AnalysisResult analysisResult = new AnalysisResult();
        analysisResult.setResultQuestionList(new ArrayList<>());
        try {
            Workbook workbook = new XSSFWorkbook(file);
            Sheet responseSheet = workbook.getSheetAt(RESPONSE_SHEET_POSITION);
            Row metadataRow = responseSheet.getRow(METADATA_ROW_POSITION);
            Row responsesRow = responseSheet.getRow(RESPONSES_POSITION);

            initAnalysisResult(analysisResult, metadataRow);
            extractQuestionsCellIndex(analysisResult, metadataRow);
            extractResponses(analysisResult, responsesRow, notationQuestions);
            subjects = analyseRespondersResponses(responseSheet, analysisResult, notationQuestions);
        } catch (Exception exception) {
            log.error("Error analysing file ", exception);
        }

        return subjects;

    }

    private List<Subject> analyseRespondersResponses(Sheet responseSheet, AnalysisResult analysisResult, List<NotationQuestion> notationQuestions) {
        List<Subject> subjects = new ArrayList<>();
        DecimalFormat df = new DecimalFormat("#");
        df.setMaximumFractionDigits(0);
        for (Row row : responseSheet) {
            if (row.getRowNum() > RESPONSES_POSITION) {
                Subject subject = new Subject();
                subject.setId(df.format(row.getCell(SUBJECT_RESPONDENT_ID_CELL_INDEX).getNumericCellValue()));
                subject.setQuestions(new ArrayList<>());
                analysisResult.getResultQuestionList().forEach(resultQuestion -> {
                    Integer startIndex = resultQuestion.getStartIndex();
                    Integer endIndex = resultQuestion.getEndIndex();
                    NotationQuestion notationQuestion = NotationQuestion.builder()
                            .value(resultQuestion.getValue())
                            .section(resultQuestion.getSection())
                            .subSection(resultQuestion.getSubSection())
                            .questionType(getQuestionType(notationQuestions, resultQuestion))
                            .responses(new ArrayList<>())
                            .build();
                    for (int counter = startIndex; counter <= endIndex; counter++) {
                        Cell cell = row.getCell(counter);
                        String val = null;
                        if (cell != null) {
                            if (CellType.STRING.equals(cell.getCellType())) {
                                val = cell.getStringCellValue();
                            } else if (CellType.NUMERIC.equals(cell.getCellType())) {
                                val = String.valueOf(cell.getNumericCellValue());
                            }

                            notationQuestion.getResponses().add(
                                    Response.builder()
                                            .value(val)
                                            .note(getNote(notationQuestions, notationQuestion, val))
                                            .build()
                            );
                        }
                    }
                    subject.getQuestions().add(notationQuestion);
                });
                subjects.add(subject);
            }
        }
        return subjects;
    }

    private Integer getNote(List<NotationQuestion> notationQuestions, NotationQuestion notationQuestion, String val) {
        if (QuestionType.STARS.equals(notationQuestion.getQuestionType())) {
            return (int) Double.parseDouble(val);
        }
        NotationQuestion question = notationQuestions.stream()
                .filter(nq -> StringUtils.containsIgnoreCase(
                        strip(removeLineIndent(nq.getValue())), strip(removeLineIndent(notationQuestion.getValue()))
                        )
                )
                .findFirst()
                .get();
        Optional<Response> response = question.getResponses()
                .stream()
                .filter(resp -> StringUtils.equalsIgnoreCase(strip(removeLineIndent(resp.getValue())), removeLineIndent(strip(val))))
                .findFirst();
//        if(QuestionType.CHECKBOX.equals(notationQuestion.getQuestionType())){
//
//        }else {
//
//        }
        return response.isPresent() ? response.get().getNote() :
                question.getResponses()
                        .stream()
                        .filter(q -> StringUtils.containsIgnoreCase(q.getValue(), "other"))
                        .findFirst()
                        .get()
                        .getNote();
    }

    private QuestionType getQuestionType(List<NotationQuestion> notationQuestions, ResultQuestion resultQuestion) {
        return notationQuestions.stream()
                .filter(notationQuestion -> StringUtils.containsIgnoreCase(
                        strip(removeLineIndent(notationQuestion.getValue())), strip(removeLineIndent(resultQuestion.getValue()))
                        )
                )
                .findFirst()
                .map(NotationQuestion::getQuestionType)
                .orElse(null);
    }

    private String strip(String string) {
        return StringUtils.deleteWhitespace(string).replaceAll("\u00A0", "");
    }

    private void extractResponses(AnalysisResult analysisResult, Row responsesRow, List<NotationQuestion> notationQuestions) {
        List<ResultQuestion> resultQuestionList = analysisResult.getResultQuestionList();
        for (ResultQuestion resultQuestion : resultQuestionList) {
            Optional<NotationQuestion> notationQuestion = notationQuestions.stream()
                    .filter(nq -> StringUtils.containsIgnoreCase(strip(nq.getValue()), strip(resultQuestion.getValue())))
                    .findAny();
            resultQuestion.setSection(notationQuestion.get().getSection());
            resultQuestion.setSubSection(notationQuestion.get().getSubSection());
            List<Response> responseList = new ArrayList<>();
            for (int counter = resultQuestion.getStartIndex(); counter <= resultQuestion.getEndIndex(); counter++) {
                Cell cell = responsesRow.getCell(counter);
                String val;
                if (CellType.STRING.equals(cell.getCellType())) {
                    val = cell.getStringCellValue();
                } else {
                    val = String.valueOf(cell.getNumericCellValue());
                }

                responseList.add(Response.builder()
                        .value(val)
                        .build());
            }
            resultQuestion.setResponses(responseList);
        }
    }

    private void initAnalysisResult(AnalysisResult analysisResult, Row metadataRow) {
        for (Cell currentCell : metadataRow) {
            if (currentCell.getColumnIndex() > QUESTION_START_POSITION) {
                ResultQuestion resultQuestion = new ResultQuestion();
                resultQuestion.setStartIndex(currentCell.getColumnIndex());
                resultQuestion.setValue(removeLineIndent(currentCell.getStringCellValue()));
                analysisResult.getResultQuestionList().add(resultQuestion);
            }
        }
    }

    private String removeLineIndent(String string) {
        return string.replaceAll("\n", "").replaceAll("\r", "");
    }

    private void extractQuestionsCellIndex(AnalysisResult analysisResult, Row metadataRow) {
        for (int counter = 0; counter < analysisResult.getResultQuestionList().size(); counter++) {
            if (counter + 1 != analysisResult.getResultQuestionList().size()) {
                analysisResult.getResultQuestionList()
                        .get(counter)
                        .setEndIndex(analysisResult.getResultQuestionList()
                                .get(counter + 1)
                                .getStartIndex() - 1);
            }
        }
        analysisResult.getResultQuestionList().get(analysisResult.getResultQuestionList().size() - 1).setEndIndex((int) metadataRow.getLastCellNum() - 1);
    }

    public FinalResult analyse(MultipartFile multipartFile) {
        File file = new File(Objects.requireNonNull(multipartFile.getOriginalFilename()));
        List<NotationQuestion> notationQuestions = new ArrayList<>();
        NotationQuestion notationQuestion = null;
        List<Subject> subjects = null;
        FinalResult finalResult = new FinalResult();
        try {
            FileUtils.writeByteArrayToFile(file, multipartFile.getBytes());
            Workbook workbook = new XSSFWorkbook(file);
            Sheet sheet = workbook.getSheetAt(NOTATION_STYLESHEET_POSITION);
            for (Row row : sheet) {
                if (row.getRowNum() > METADATA_ROW_POSITION) {
                    Cell sectionCell = row.getCell(SECTION_CELL_INDEX);
                    Cell subsectionCell = row.getCell(SUBSECTION_CELL_INDEX);
                    Cell questionTypeCell = row.getCell(QUESTION_TYPE_CELL_INDEX);
                    Cell noteCell = row.getCell(NOTE_CELL_INDEX);
                    Cell questionCell = row.getCell(QUESTION_CELL_INDEX);
                    Cell responseCell = row.getCell(RESPONSE_CELL_INDEX);
                    if (sectionCell != null && CellType.STRING.equals(sectionCell.getCellType())) {
                        notationQuestion = new NotationQuestion();
                        notationQuestion.setSection(sectionCell.getStringCellValue());
                        notationQuestion.setSubSection(subsectionCell.getStringCellValue());
                        notationQuestion.setQuestionType(QuestionType.valueOf(questionTypeCell.getStringCellValue()));
                        notationQuestion.setValue(questionCell.getStringCellValue().replaceAll("\n", "").replaceAll("\r", ""));
                        notationQuestion.setResponses(new ArrayList<>());
                        notationQuestions.add(notationQuestion);
                    } else {
                        int noteVal;
                        if (CellType.STRING.equals(noteCell.getCellType())) {
                            noteVal = 0;
                        } else {
                            noteVal = (int) noteCell.getNumericCellValue();
                        }
                        String responseVal;
                        if (responseCell != null && CellType.STRING.equals(responseCell.getCellType())) {
                            responseVal = responseCell.getStringCellValue();
                        } else {
                            responseVal = String.valueOf(responseCell.getNumericCellValue());
                        }
                        notationQuestion.getResponses().add(
                                Response.builder()
                                        .value(responseVal)
                                        .note(noteVal)
                                        .build());
                    }
                }
            }
            subjects = analyseResponses(file, notationQuestions);
            calculateNoteByQuestion(subjects);
            finalResult.setIndividualStatistics(subjects);
            finalResult.setSubSectionStatistics(calculateSubsectionNotes(subjects));
            finalResult.setSectionStatistics(calculateSectionNotes(subjects));
            workbook.close();
        } catch (IOException | InvalidFormatException exception) {
            log.error("error saving notation file", exception);
        } finally {
            log.info("File " + file.getName() + " deleted : " + file.delete());
        }

        return finalResult;
    }

    private Map<String, IntSummaryStatistics> calculateSubsectionNotes(List<Subject> subjects) {
        return subjects.stream()
                .map(Subject::getQuestions)
                .flatMap(Collection::stream)
                .collect(Collectors.groupingBy(NotationQuestion::getSubSection, Collectors.summarizingInt(NotationQuestion::getNote)));
    }

    private Map<String, IntSummaryStatistics> calculateSectionNotes(List<Subject> subjects) {
        return subjects.stream()
                .map(Subject::getQuestions)
                .flatMap(Collection::stream)
                .collect(Collectors.groupingBy(NotationQuestion::getSection, Collectors.summarizingInt(NotationQuestion::getNote)));
    }

    private void calculateNoteByQuestion(List<Subject> subjects) {
        subjects.forEach(subject -> subject.getQuestions().forEach(notationQuestion -> {
            notationQuestion.setNote(notationQuestion.getResponses().stream()
                    .mapToInt(Response::getNote)
                    .sum());
        }));
    }
}
