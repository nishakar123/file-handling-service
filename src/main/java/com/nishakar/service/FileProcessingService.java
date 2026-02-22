package com.nishakar.service;

import org.springframework.web.multipart.MultipartFile;

public interface FileProcessingService {
    void processFile(MultipartFile file);
}
