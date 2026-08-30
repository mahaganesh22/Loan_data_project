package com.loan.loan_data_project.global_exception.exception;

import org.springframework.http.HttpStatus;

public class RecordHashGenerationException extends BaseApiException {

    public RecordHashGenerationException(Throwable cause) {
        super("Unable to generate record hash", HttpStatus.INTERNAL_SERVER_ERROR, cause);
    }
}
