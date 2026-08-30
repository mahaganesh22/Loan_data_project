package com.loan.loan_data_project.global_exception.exception;

import org.springframework.http.HttpStatus;

public class InvalidCredentialsException extends BaseApiException {

    public InvalidCredentialsException() {
        super(
                "Username or password is incorrect. Please check your credentials and try again.",
                HttpStatus.UNAUTHORIZED,
                "INVALID_CREDENTIALS"
        );
    }
}
