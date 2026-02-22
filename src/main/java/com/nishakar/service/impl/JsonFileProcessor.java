package com.nishakar.service.impl;

import com.nishakar.entity.KafkaEvent;
import com.nishakar.repository.KafkaEventRepository;
import com.nishakar.service.FileProcessingService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import tools.jackson.databind.MappingIterator;
import tools.jackson.databind.ObjectMapper;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class JsonFileProcessor implements FileProcessingService {

    private final KafkaEventRepository kafkaEventRepository;
    private final ObjectMapper mapper;

    @Override
    @Transactional
    public void processFile(MultipartFile file) {

        try (InputStream is = file.getInputStream()) {

            MappingIterator<KafkaEvent> iterator =
                    mapper.readerFor(KafkaEvent.class).readValues(is);

            List<KafkaEvent> batch = new ArrayList<>();
            int batchSize = 1000;

            while(iterator.hasNext()) {

                batch.add(iterator.next());

                if(batch.size() == batchSize) {
                    kafkaEventRepository.saveAll(batch);
                    batch.clear();
                }
            }

            if(!batch.isEmpty())
                kafkaEventRepository.saveAll(batch);

        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
