package com.nishakar.service.load;

import com.nishakar.entity.KafkaEvent;
import com.nishakar.repository.KafkaEventRepository;
import com.opencsv.CSVReader;
import com.opencsv.CSVReaderBuilder;
import com.opencsv.exceptions.CsvValidationException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.io.InputStreamReader;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class CsvImportService {

    private final KafkaEventRepository kafkaEventRepository;

    private static final String CSV_FILE_PATH = "csv/test-data.csv";
    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    public Integer importCsv() {
        List<KafkaEvent> events = new ArrayList<>();
        // ✅ Load from classpath (works in both IDE and packaged JAR)
        Resource resource = new ClassPathResource(CSV_FILE_PATH);
        try (CSVReader reader = new CSVReaderBuilder(new InputStreamReader(resource.getInputStream()))
                .withSkipLines(1) // skip header row
                .build()) {

            String[] line;
            while ((line = reader.readNext()) != null) {
                KafkaEvent event = mapToEvent(line);
                events.add(event);
            }

            List<KafkaEvent> saved = kafkaEventRepository.saveAll(events);
            final int size = saved.size();
            log.info("Successfully saved {} records to the database.", size);
            return size;

        } catch (IOException | CsvValidationException e) {
            log.error("Failed to read CSV file: {}", e.getMessage());
            throw new RuntimeException("CSV import failed", e);
        }
    }

    private KafkaEvent mapToEvent(String[] fields) {
        KafkaEvent event = new KafkaEvent();

        event.setFName(fields[0].trim());
        event.setLName(fields[1].trim());

        // Parse "dd/MM/yyyy" date and set time to midnight
        LocalDate date = LocalDate.parse(fields[2].trim(), FORMATTER);
        event.setDate(date);

        event.setStatus(fields[3].trim());

        return event;
    }
}
