package com.loan.loan_data_project.global_exception.exception;

import org.springframework.http.HttpStatus;

public class UnauthenticatedUserException extends BaseApiException {

    public UnauthenticatedUserException() {
        super("No authenticated user found", HttpStatus.UNAUTHORIZED);
    }
}
