package com.loan.loan_data_project.upload_file.service;

import com.loan.loan_data_project.exception.service.ExceptionService;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.loan.loan_data_project.loans.entity.Loan;
import com.loan.loan_data_project.loans.repository.LoanRepository;
import com.loan.loan_data_project.upload_file.dto.UploadResponse;
import com.loan.loan_data_project.upload_file.entity.UploadedFile;
import com.loan.loan_data_project.upload_file.enums.Status;
import com.loan.loan_data_project.upload_file.ingestion.CsvParser;
import com.loan.loan_data_project.upload_file.repository.UploadedFileRepository;
import com.loan.loan_data_project.validation.dto.ValidationError;
import com.loan.loan_data_project.validation.dto.ValidationResult;
import com.loan.loan_data_project.validation.service.LoanValidationService;

import java.io.IOException;
import java.io.Reader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;

@Service
public class UploadService {

    private final ExceptionService exceptionService;
    private final UploadedFileRepository uploadedFileRepository;
    private final LoanRepository loanRepository;
    private final CsvParser csvParser;
    private final LoanValidationService loanValidationService;

    @Value("${file.upload-dir}")
    private String uploadDirectory;

    public UploadService(
            UploadedFileRepository uploadedFileRepository,
            LoanRepository loanRepository,
            CsvParser csvParser,
            LoanValidationService loanValidationService, ExceptionService exceptionService
            
    ) {
        this.uploadedFileRepository = uploadedFileRepository;
        this.loanRepository = loanRepository;
        this.csvParser = csvParser;
        this.loanValidationService = loanValidationService;
        this.exceptionService = exceptionService;
    }

    public UploadResponse upload(MultipartFile file) throws IOException {

        if (file.isEmpty()) {
            throw new IllegalArgumentException("Uploaded file is empty");
        }

        String originalFileName = file.getOriginalFilename();

        if (originalFileName == null ||
                !originalFileName.toLowerCase().endsWith(".csv")) {

            throw new IllegalArgumentException("Only CSV files are allowed");
        }

        Path uploadPath = Paths.get(uploadDirectory);

        Files.createDirectories(uploadPath);

        Path filePath = uploadPath.resolve(originalFileName);

        Files.copy(
                file.getInputStream(),
                filePath
        );

        UploadedFile uploadedFile = new UploadedFile();

        uploadedFile.setFileName(originalFileName);
        uploadedFile.setFilePath(filePath.toString());
        uploadedFile.setUploadedAt(LocalDateTime.now());
        uploadedFile.setStatus(Status.PROCESSING);
        uploadedFile.setTotalRows(0);
        uploadedFile.setSuccessfulRows(0);
        uploadedFile.setFailedRows(0);

        uploadedFile = uploadedFileRepository.save(uploadedFile);

        int totalRows = 0;
        int successfulRows = 0;
        int failedRows = 0;

        try (
                Reader reader = Files.newBufferedReader(filePath);
                CSVParser parser = csvParser.createParser(reader)
        ) {

            for (CSVRecord record : parser) {

                totalRows++;

                try {

                    Loan loan = csvParser.parseLoan(record);

                    loan.setUploadedFile(uploadedFile);

                    ValidationResult validationResult = loanValidationService.validate(loan);

                    if (validationResult.isValid()) {
                        
                        loanRepository.save(loan);
                        successfulRows++;
                    } else {
                        failedRows++;

                        loanRepository.save(loan);

                        for (ValidationError error : validationResult.getErrors()) {
                            exceptionService.createException(loan, error);
                        }
                    }

                } catch (Exception e) {

                    failedRows++;

                    System.out.println(
                            "Failed to import row "
                                    + record.getRecordNumber()
                                    + ": "
                                    + e.getMessage()
                    );
                }
            }
        }

        uploadedFile.setTotalRows(totalRows);
        uploadedFile.setSuccessfulRows(successfulRows);
        uploadedFile.setFailedRows(failedRows);

        if (failedRows == 0) {
            uploadedFile.setStatus(Status.COMPLETED);
        } else if (successfulRows == 0) {
            uploadedFile.setStatus(Status.FAILED);
        } else {
            uploadedFile.setStatus(Status.COMPLETED_WITH_ERRORS);
        }

        uploadedFileRepository.save(uploadedFile);

        return new UploadResponse(
                originalFileName,
                totalRows,
                successfulRows,
                failedRows,
                uploadedFile.getStatus()
        );
    }
}
