package com.loan.loan_data_project.global_exception.dto;

import java.time.LocalDateTime;

import org.springframework.http.HttpStatus;

public final class ErrorResponses {

    private ErrorResponses() {
    }

    public static ErrorResponse of(HttpStatus status, String message, String path, String code) {
        return new ErrorResponse(
                LocalDateTime.now(),
                status.value(),
                status.getReasonPhrase(),
                message,
                path,
                code
        );
    }
}
