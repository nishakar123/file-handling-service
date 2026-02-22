package com.nishakar.controller;

import com.nishakar.factory.FileParserFactory;
import com.nishakar.service.FileProcessingService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.util.Objects;

@Slf4j
@RestController
@RequestMapping("/api/file-handling-service")
@RequiredArgsConstructor
public class FileUploadController {

    private final FileParserFactory factory;

    @PostMapping("/upload")
    public ResponseEntity<String> uploadFile(
            @RequestParam("file") MultipartFile file) {

        FileProcessingService processor =
                factory.getProcessor(Objects.requireNonNull(file.getOriginalFilename()));

        processor.processFile(file);

        return ResponseEntity.ok("File processed successfully");
    }
}
