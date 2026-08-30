package com.loan.loan_data_project.global_exception.exception;

import org.springframework.http.HttpStatus;

public class AccessForbiddenException extends BaseApiException {

    public AccessForbiddenException() {
        super(
                "You do not have permission to perform this action with your current role.",
                HttpStatus.FORBIDDEN,
                "ACCESS_DENIED"
        );
    }
}
