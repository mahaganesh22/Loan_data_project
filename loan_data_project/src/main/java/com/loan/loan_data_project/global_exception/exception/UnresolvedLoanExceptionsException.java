package com.loan.loan_data_project.global_exception.exception;

import org.springframework.http.HttpStatus;

public class UnresolvedLoanExceptionsException extends BaseApiException {

    public UnresolvedLoanExceptionsException() {
        super("Loan has unresolved exceptions", HttpStatus.BAD_REQUEST);
    }
}
