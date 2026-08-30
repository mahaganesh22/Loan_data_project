package com.loan.loan_data_project.global_exception.exception;

import org.springframework.http.HttpStatus;

public class ExceptionAlreadyProcessedException extends BaseApiException {

    public ExceptionAlreadyProcessedException() {
        super("Exception has already been processed", HttpStatus.CONFLICT);
    }
}
