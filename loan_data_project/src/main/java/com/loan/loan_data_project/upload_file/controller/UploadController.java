package com.loan.loan_data_project.upload_file.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import com.loan.loan_data_project.upload_file.dto.UploadResponse;
import com.loan.loan_data_project.upload_file.service.UploadService;

@RestController
@RequestMapping("/api/uploads")
public class UploadController {

    private final UploadService uploadService;

    public UploadController(UploadService uploadService) {
        this.uploadService = uploadService;
    }

    @PostMapping
    public ResponseEntity<UploadResponse> uploadFile(
            @RequestParam("file") MultipartFile file
    ) throws Exception {

        UploadResponse response = uploadService.upload(file);

        return ResponseEntity.ok(response);
    }
}
