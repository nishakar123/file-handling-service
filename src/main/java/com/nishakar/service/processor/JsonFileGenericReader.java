package com.nishakar.service.processor;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.nishakar.commons.records.files.FileData;
import com.nishakar.commons.records.files.JsonFileData;
import com.nishakar.service.FileReaderService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.util.*;

@Slf4j
@Service
@RequiredArgsConstructor
public class JsonFileGenericReader implements FileReaderService<JsonFileData> {

    private final ObjectMapper objectMapper;

    @Override
    public FileData<JsonFileData> readFile(MultipartFile file) {
        log.info("JsonFileGenericReader -> readFile !");
        try (InputStream is = file.getInputStream()) {

            JsonNode rootNode = objectMapper.readTree(is);

            // If JSON is array
            if (rootNode.isArray()) {
                List<Map<String, Object>> list = new ArrayList<>();
                for (JsonNode node : rootNode) {
                    Map<String, Object> map =
                            objectMapper.convertValue(
                                    node,
                                    new TypeReference<>() {});
                    list.add(map);
                }
                JsonFileData jsonFileData = new JsonFileData(list.size(), list);
                return new FileData<>(file.getOriginalFilename(), list.size(),  jsonFileData);
            }
            // If JSON is single object
            else if (rootNode.isObject()) {
                final Map<String, Object> stringObjectMap = objectMapper.convertValue(
                        rootNode,
                        new TypeReference<Map<String, Object>>() {
                        });
                JsonFileData jsonFileData = new JsonFileData(stringObjectMap.size(), List.of(stringObjectMap));
                return new FileData<>(file.getOriginalFilename(), stringObjectMap.size(),  jsonFileData);
            }
            else {
                throw new RuntimeException("Invalid JSON structure");
            }
        } catch (Exception e) {
            log.error("Exception occurred while reading file", e);
            throw new RuntimeException("Error reading JSON file", e);
        }
    }

    public FileData<JsonFileData> readFileData(MultipartFile file) {
        log.info("JsonFileGenericReader -> readFileData !");
        try(InputStream is = file.getInputStream()) {
            // Read as JsonNode to handle any structure dynamically
            JsonNode rootNode = objectMapper.readTree(is);

            if (rootNode.isEmpty()) {
                throw new RuntimeException("JSON file is empty: " + file.getOriginalFilename());
            }

            List<Map<String, Object>> result = new ArrayList<>();

            // Handle both JSON array [ {...}, {...} ] and single object { ... }
            if (rootNode.isArray()) {
                for (JsonNode node : rootNode) {
                    result.add(parseNode(node));
                }
            } else if (rootNode.isObject()) {
                result.add(parseNode(rootNode));
            } else {
                throw new RuntimeException("Unsupported JSON structure in: " + file.getOriginalFilename());
            }
            log.info("Total records read: {}", result.size());
            log.info("Attributes found: {}", result.isEmpty() ? "none" : result.get(0).keySet());
            JsonFileData jsonFileData = new JsonFileData(result.size(), result);
            return new FileData<>(file.getOriginalFilename(), result.size(), jsonFileData);
        } catch (IOException e) {
            log.error("Failed to read JSON file: {}", e.getMessage());
            throw new RuntimeException("JSON read failed", e);
        }
    }

    // Dynamically parse each node — handles any number of attributes
    private Map<String, Object> parseNode(JsonNode node) {
        Map<String, Object> record = new LinkedHashMap<>();
        ObjectNode objectNode = (ObjectNode) node;
        for (Map.Entry<String, JsonNode> entry : objectNode.properties()) {
            String key = entry.getKey();
            JsonNode valueNode = entry.getValue();
            record.put(key, extractValue(valueNode));
        }
        return record;
    }

    // Recursively resolve value based on its type
    private Object extractValue(JsonNode node) {
        if (node.isNull())            return null;
        if (node.isBoolean())         return node.asBoolean();
        if (node.isInt())             return node.asInt();
        if (node.isLong())            return node.asLong();
        if (node.isDouble())          return node.asDouble();
        if (node.isTextual())         return node.asText();
        if (node.isArray())           return parseArray(node);
        if (node.isObject())          return parseNode(node);   // handles nested objects
        return node.asText();
    }

    private List<Object> parseArray(JsonNode arrayNode) {
        List<Object> list = new ArrayList<>();
        arrayNode.forEach(element -> list.add(extractValue(element)));
        return list;
    }
}
