package com.nishakar.controller;

import com.nishakar.service.load.CsvImportService;
import com.nishakar.service.load.JsonImportService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RequestMapping("/api/file-handling-service")
@RestController
@RequiredArgsConstructor
public class LoadFileDataToDb {

    private final CsvImportService csvImportService;

    private final JsonImportService jsonImportService;

    @GetMapping("/csv")
    public ResponseEntity<String> loadCsvFileDataToDb() {
        log.info("LoadFileDataToDb -> loadCsvFileDataToDb !");
        final Integer size = csvImportService.importCsv();
        return ResponseEntity.ok().body("Successfully saved " + size + " records to the database.");
    }

    @GetMapping("/json")
    private ResponseEntity<String> loadJsonFileDataToDb() {
        log.info("LoadFileDataToDb -> loadJsonFileDataToDb !");
        final Integer size = jsonImportService.importJson();
        return ResponseEntity.ok().body("Successfully saved " + size + " records to the database.");
    }
}
