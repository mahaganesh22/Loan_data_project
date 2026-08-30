package com.loan.loan_data_project.upload_file.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.loan.loan_data_project.upload_file.entity.UploadedFile;


public interface UploadedFileRepository extends JpaRepository<UploadedFile, Long> {
    
}
