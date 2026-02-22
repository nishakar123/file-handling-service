package com.nishakar.service.load;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.nishakar.commons.records.kafka.KafkaEventRecord;
import com.nishakar.commons.utils.singleton.CustomObjectMapper;
import com.nishakar.entity.KafkaEvent;
import com.nishakar.repository.KafkaEventRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class JsonImportService {

    private final KafkaEventRepository repository;

    private static final String JSON_FILE_PATH = "json/test-data.json";

    public Integer importJson() {
        try {
            Resource resource = new ClassPathResource(JSON_FILE_PATH);
            final ObjectMapper objectMapper = CustomObjectMapper.INSTANCE.get();
            // Deserialize JSON array into list of records
            List<KafkaEventRecord> records = objectMapper.readValue(
                    resource.getInputStream(),
                    objectMapper.getTypeFactory().constructCollectionType(List.class, KafkaEventRecord.class)
            );

            List<KafkaEvent> events = records.stream()
                    .peek(e -> System.out.println("Event: " + e))
                    .map(this::mapToEvent)
                    .collect(Collectors.toList());

            List<KafkaEvent> saved = repository.saveAll(events);
            final int size = saved.size();
            log.info("Successfully saved {} JSON records to the database.", size);
            return size;

        } catch (Exception e) {
            log.error("Failed to read JSON file: {}", e.getMessage());
            throw new RuntimeException("JSON import failed", e);
        }
    }

    private KafkaEvent mapToEvent(KafkaEventRecord record) {
        KafkaEvent event = new KafkaEvent();

        event.setFName(record.fName());
        event.setLName(record.lName());
        LocalDate date = LocalDate.parse(record.date().toString());
        event.setDate(date);

        // Boolean -> String to match the entity field type
        event.setStatus(String.valueOf(record.status()));

        return event;
    }
}
