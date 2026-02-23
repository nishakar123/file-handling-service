package com.nishakar.service;

import com.nishakar.commons.records.files.FileData;
import org.springframework.web.multipart.MultipartFile;

public interface FileReaderService<T> {
    FileData<T> readFile(MultipartFile file);
}
