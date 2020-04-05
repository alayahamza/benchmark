package com.alenia.benchmark.controller;

import com.alenia.benchmark.model.FinalResult;
import com.alenia.benchmark.service.NotationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping(value = "/api/v1/files")
public class BenchmarkController {

    private final NotationService notationService;

    @Autowired
    public BenchmarkController(NotationService notationService) {
        this.notationService = notationService;
    }

    @PostMapping(value = "benchmark")
    public ResponseEntity<FinalResult> uploadNotationFile(@RequestParam("file") MultipartFile file) {
        return ResponseEntity.ok().body(notationService.analyse(file));
    }
}
