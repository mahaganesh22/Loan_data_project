package com.loan.loan_data_project.upload_file.dto;

import com.loan.loan_data_project.upload_file.enums.Status;


public class UploadResponse {

    private String fileName;
    private int totalRows;
    private int successfulRows;
    private int failedRows;
    private Status status;

    public UploadResponse(
            String fileName,
            int totalRows,
            int successfulRows,
            int failedRows,
            Status status
    ) {
        this.fileName = fileName;
        this.totalRows = totalRows;
        this.successfulRows = successfulRows;
        this.failedRows = failedRows;
        this.status = status;
    }

    public String getFileName() {
        return fileName;
    }

    public int getTotalRows() {
        return totalRows;
    }

    public int getSuccessfulRows() {
        return successfulRows;
    }

    public int getFailedRows() {
        return failedRows;
    }

    public Status getStatus() {
        return status;
    }
}
