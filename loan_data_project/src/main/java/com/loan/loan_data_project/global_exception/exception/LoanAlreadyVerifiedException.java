package com.loan.loan_data_project.global_exception.exception;

import org.springframework.http.HttpStatus;

public class LoanAlreadyVerifiedException extends BaseApiException {

    public LoanAlreadyVerifiedException() {
        super("Loan is already verified", HttpStatus.CONFLICT);
    }
}
