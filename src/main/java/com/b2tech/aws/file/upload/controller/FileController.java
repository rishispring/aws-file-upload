package com.b2tech.aws.file.upload.controller;

import com.b2tech.aws.file.upload.service.FileService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.Map;

@RestController
public class FileController {

    @Autowired
    FileService fileService = new FileService();

    @PostMapping("/api/uploadFile")
    public ResponseEntity<Map<String, String>> uploadFile(@RequestParam("file") MultipartFile multipartFile){
        Map<String, String> response = fileService.save(multipartFile);
        return ResponseEntity.status(HttpStatus.OK)
                .body(response);
    }
}
