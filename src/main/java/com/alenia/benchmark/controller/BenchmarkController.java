package com.alenia.benchmark.controller;

import com.alenia.benchmark.model.Data;
import com.alenia.benchmark.service.BenchmarkService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping(value = "/api/v1")
public class BenchmarkController {

    private final BenchmarkService benchmarkService;

    @Autowired
    public BenchmarkController(BenchmarkService benchmarkService) {
        this.benchmarkService = benchmarkService;
    }

    @GetMapping(value = "awake")
    public ResponseEntity<Boolean> isAwake() {
        return ResponseEntity.ok().body(true);
    }

    @PostMapping(value = "benchmark")
    public ResponseEntity<Data> uploadNotationFile(@RequestParam("file") MultipartFile file) {
        return ResponseEntity.ok().body(benchmarkService.analyse(file));
    }
}
