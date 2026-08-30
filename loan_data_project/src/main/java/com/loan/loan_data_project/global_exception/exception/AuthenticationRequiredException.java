package com.loan.loan_data_project.global_exception.exception;

import org.springframework.http.HttpStatus;

public class AuthenticationRequiredException extends BaseApiException {

    public AuthenticationRequiredException() {
        super(
                "You must sign in to access this resource.",
                HttpStatus.UNAUTHORIZED,
                "AUTHENTICATION_REQUIRED"
        );
    }
}
