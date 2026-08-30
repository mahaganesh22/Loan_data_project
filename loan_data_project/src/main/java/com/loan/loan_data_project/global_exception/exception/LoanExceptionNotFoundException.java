package com.loan.loan_data_project.global_exception.exception;

import org.springframework.http.HttpStatus;

public class LoanExceptionNotFoundException extends BaseApiException {

    public LoanExceptionNotFoundException(Long exceptionId) {
        super("Exception not found with id: " + exceptionId, HttpStatus.NOT_FOUND);
    }
}
