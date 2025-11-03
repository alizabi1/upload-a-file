package com.example.demo.controller;

import com.example.demo.service.FileService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import java.nio.charset.StandardCharsets;

@Controller
@RequestMapping("/v1/files/")
public class FileController {

    @Autowired
    private FileService fileService;

    @PostMapping(path = "upload")
    public ResponseEntity<byte[]> uploadFile(@RequestParam("file") MultipartFile file,
                           @RequestParam(value = "validate", required = false, defaultValue = "false") boolean validate){
        String content = fileService.parseFile(file, validate);
        return ResponseEntity.ok()
                .contentType(MediaType.APPLICATION_JSON)
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"OutcomeFile.json\"")
                .body(content.getBytes(StandardCharsets.UTF_8));
    }

}
