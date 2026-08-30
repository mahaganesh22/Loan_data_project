package com.loan.loan_data_project.global_exception.exception;

import org.springframework.http.HttpStatus;

public class SessionExpiredException extends BaseApiException {

    public SessionExpiredException() {
        super(
                "Your session is invalid or has expired. Please sign in again.",
                HttpStatus.UNAUTHORIZED,
                "SESSION_EXPIRED"
        );
    }
}
