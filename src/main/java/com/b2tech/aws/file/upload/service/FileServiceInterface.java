package com.b2tech.aws.file.upload.service;

import org.springframework.web.multipart.MultipartFile;

import java.util.Map;

public interface FileServiceInterface {

    Map<String, String> save(MultipartFile file);
//    byte[] downloadFile(String fileName);
    Map<String, String> deleteFile(String fileName);
    void listFiles();
}
