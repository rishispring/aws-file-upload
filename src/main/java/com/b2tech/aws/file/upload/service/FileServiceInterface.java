package com.b2tech.aws.file.upload.service;

import org.springframework.web.multipart.MultipartFile;

import java.util.Map;

public interface FileServiceInterface {

    Map<String, String> save(MultipartFile file);

     Map<String, String> deleteFile(String fileName);

     byte[] downloadFile(String fileName);

//    byte[] downloadFile(String fileName);
//
//    String deleteFile(String fileName);
//
//    List<String> listAllFiles();
}
