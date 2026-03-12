package com.nishakar.controller;

import com.nishakar.service.batch.BatchJobLauncherService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.List;
import java.util.Map;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/file-handling-service")
public class EmployeeCsvUploadController {

    private final BatchJobLauncherService batchJobLauncherService;

    @PostMapping("/batch/upload/employees")
    public ResponseEntity<Map<String, Object>> uploadFile(@RequestParam("file") MultipartFile file) throws Exception {
        log.info("EmployeeCsvUploadController -> uploadFile");

        Path path = Paths.get("uploads/" + file.getOriginalFilename());
        Files.createDirectories(path.getParent());

        Files.copy(file.getInputStream(),
                path,
                StandardCopyOption.REPLACE_EXISTING);

        final Map<String, Object> stringObjectMap = batchJobLauncherService.startJob(path.toString());

        return ResponseEntity.ok(stringObjectMap);
    }

    @GetMapping("/batch/status/{executionId}")
    public ResponseEntity<Map<String, Object>> getJobStatus(@PathVariable Long executionId) {
        log.info("EmployeeCsvUploadController -> getJobStatus");
        final Map<String, Object> jobStatus = batchJobLauncherService.getJobStatus(executionId);
        return ResponseEntity.ok(jobStatus);
    }

    @GetMapping("/batch/instances")
    public ResponseEntity<List<Map<String, Object>>> listInstances() {
        log.info("EmployeeCsvUploadController -> listInstances");
        final List<Map<String, Object>> instances = batchJobLauncherService.listInstances();
        return ResponseEntity.ok(instances);
    }
}
