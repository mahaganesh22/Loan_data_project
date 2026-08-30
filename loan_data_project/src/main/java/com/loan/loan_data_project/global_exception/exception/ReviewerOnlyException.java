package com.loan.loan_data_project.global_exception.exception;

import org.springframework.http.HttpStatus;

public class ReviewerOnlyException extends BaseApiException {

    public ReviewerOnlyException() {
        super("Only reviewers can verify loans", HttpStatus.FORBIDDEN);
    }
}
