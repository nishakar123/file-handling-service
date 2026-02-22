package com.nishakar.factory;

import com.nishakar.service.impl.CsvFileProcessor;
import com.nishakar.service.FileProcessingService;
import com.nishakar.service.impl.JsonFileProcessor;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class FileParserFactory {

    private final CsvFileProcessor csvProcessor;
    private final JsonFileProcessor jsonProcessor;

    public FileProcessingService getProcessor(String fileName) {

        if(fileName.endsWith(".csv"))
            return csvProcessor;
        else if(fileName.endsWith(".json"))
            return jsonProcessor;

        throw new RuntimeException("Unsupported File Format");
    }
}
