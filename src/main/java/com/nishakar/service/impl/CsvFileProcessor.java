package com.nishakar.service.impl;

import com.nishakar.entity.KafkaEvent;
import com.nishakar.repository.KafkaEventRepository;
import com.nishakar.service.FileProcessingService;
import com.opencsv.CSVReader;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.Reader;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class CsvFileProcessor implements FileProcessingService {

    private final KafkaEventRepository kafkaEventRepository;

    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    @Override
    @Transactional
    public void processFile(MultipartFile file) {

        try (Reader reader = new BufferedReader(
                new InputStreamReader(file.getInputStream()))) {

            CSVReader csvReader = new CSVReader(reader);

            List<KafkaEvent> batch = new ArrayList<>();
            String[] row;
            int batchSize = 1000;

            csvReader.readNext(); // Skip header

            while ((row = csvReader.readNext()) != null) {

                KafkaEvent event = KafkaEvent.builder()
                        .fName(row[0])
                        .lName(row[1])
                        .date(LocalDate.parse(row[2].trim(), FORMATTER))
                        .status(row[3])
                        .build();

                batch.add(event);

                if(batch.size() == batchSize) {
                    kafkaEventRepository.saveAll(batch);
                    batch.clear();
                }
            }

            if(!batch.isEmpty())
                kafkaEventRepository.saveAll(batch);

        } catch (Exception e) {
            log.error("Error caught while reading file and storing the data to file : " + e);
        }
    }
}
