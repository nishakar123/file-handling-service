package com.nishakar.controller;

import com.nishakar.commons.records.files.CsvFileData;
import com.nishakar.commons.records.files.FileData;
import com.nishakar.commons.records.files.JsonFileData;
import com.nishakar.service.processor.CsvFileGenericReader;
import com.nishakar.service.processor.JsonFileGenericReader;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/file-handling-service")
public class FileReaderController<T> {

    private final CsvFileGenericReader csvFileGenericReader;

    private final JsonFileGenericReader jsonFileGenericReader;

    @PostMapping("/read/csv")
    public FileData<CsvFileData> getCsvFileData(@RequestParam("file") MultipartFile file) {
        log.info("FileReaderController -> getCsvFileData !");
        final FileData<CsvFileData> csvFileData = csvFileGenericReader.readFile(file);
        return csvFileData;
    }

    @PostMapping("/read/json/v1")
    public FileData<JsonFileData> getJsonFileDataV1(@RequestParam("file") MultipartFile file) {
        log.info("FileReaderController -> getJsonFileDataV1 !");
        final FileData<JsonFileData> jsonFileData = jsonFileGenericReader.readFile(file);
        return jsonFileData;
    }

    @PostMapping("/read/json/v2")
    public FileData<JsonFileData> getJsonFileDataV2(@RequestParam("file") MultipartFile file) {
        log.info("FileReaderController -> getJsonFileDataV2 !");
        final FileData<JsonFileData> jsonFileData = jsonFileGenericReader.readFileData(file);
        return jsonFileData;
    }
}
