package com.loan.loan_data_project.global_exception.exception;

import org.springframework.http.HttpStatus;

public class LoanNotFoundException extends BaseApiException {

    public LoanNotFoundException(Long loanId) {
        super("Loan not found with id: " + loanId, HttpStatus.NOT_FOUND);
    }
}
