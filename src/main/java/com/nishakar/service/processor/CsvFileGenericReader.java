package com.nishakar.service.processor;

import com.nishakar.commons.records.files.CsvFileData;
import com.nishakar.commons.records.files.FileData;
import com.nishakar.service.FileReaderService;
import com.opencsv.CSVReader;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class CsvFileGenericReader implements FileReaderService<CsvFileData> {

    @Override
    @Transactional
    public FileData<CsvFileData> readFile(MultipartFile file) {
        log.info("CsvFileGenericReader -> readFile !");
        try (Reader reader = new BufferedReader(
                new InputStreamReader(file.getInputStream()))) {

            CSVReader csvReader = new CSVReader(reader);

            List<String[]> allLines = csvReader.readAll();

            if (allLines == null || allLines.isEmpty()) {
                throw new RuntimeException("CSV file is empty: " + file.getOriginalFilename());
            }

            // First row = headers (handles any number of columns dynamically)
            List<String> headers = Arrays.stream(allLines.get(0))
                    .map(String::trim)
                    .collect(Collectors.toList());

            log.info("CSV headers found: {}", headers);

            // Remaining rows = data, mapped to their respective headers
            List<Map<String, String>> rows = new ArrayList<>();

            for (int i = 1; i < allLines.size(); i++) {
                String[] fields = allLines.get(i);
                Map<String, String> row = new LinkedHashMap<>(); // LinkedHashMap preserves column order

                for (int j = 0; j < headers.size(); j++) {
                    String value = (j < fields.length) ? fields[j].trim() : ""; // handle missing fields
                    row.put(headers.get(j), value);
                }

                rows.add(row);
            }

            log.info("Total rows read: {}", rows.size());
            CsvFileData csvFileData = new CsvFileData(rows.size(), headers, rows);
            return new FileData<>(file.getOriginalFilename(), csvFileData.rows().size(), csvFileData);
        } catch (Exception e) {
            log.error("Error caught while reading file and storing the data to file : {}", String.valueOf(e));
            throw new RuntimeException(e);
        }
    }
}
