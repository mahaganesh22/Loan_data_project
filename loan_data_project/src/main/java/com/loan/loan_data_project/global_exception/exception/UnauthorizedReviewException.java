package com.loan.loan_data_project.global_exception.exception;

import org.springframework.http.HttpStatus;

public class UnauthorizedReviewException extends BaseApiException {

    public UnauthorizedReviewException() {
        super("User is not authorized to review exceptions", HttpStatus.FORBIDDEN);
    }
}
