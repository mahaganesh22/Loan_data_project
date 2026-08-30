package com.loan.loan_data_project.global_exception.exception;

import org.springframework.http.HttpStatus;

public class FieldNotCorrectableException extends BaseApiException {

    public FieldNotCorrectableException(String fieldName) {
        super("Field cannot be corrected: " + fieldName, HttpStatus.BAD_REQUEST);
    }
}
