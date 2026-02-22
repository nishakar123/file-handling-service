package com.nishakar.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.nishakar.commons.records.StudentRecord;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;

@Slf4j
@RequestMapping("/api/file-handling-service")
@RestController
public class TestController {

    @Value("${test.name}")
    private String name;

    @Autowired
    private ObjectMapper objectMapper;

    @GetMapping("/hello")
    public String hello() throws JsonProcessingException {
        log.info("TestController -> hello !");
        log.info("TestController -> hello !, name : {}", name);
        StudentRecord studentRecord = new StudentRecord(1, "Nishakar", 23, LocalDateTime.now());
        log.info("TestController -> hello , studentRecord : {}", studentRecord);
        log.info("CustomObjectMapper -> hello ! {}", objectMapper.writeValueAsString(studentRecord));
        return "Hello, File Handling Service Called";
    }
}
