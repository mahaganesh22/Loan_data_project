package com.loan.loan_data_project.global_exception.exception;

import org.springframework.http.HttpStatus;

public class VerifiedLoanNotFoundException extends BaseApiException {

    public VerifiedLoanNotFoundException(Long id) {
        super("Verified loan not found with id: " + id, HttpStatus.NOT_FOUND);
    }
}
