package com.loan.loan_data_project.validation.dto;

import com.loan.loan_data_project.exception.enums.ExceptionSeverity;
import com.loan.loan_data_project.exception.enums.ExceptionType;

public class ValidationError {

    private final ExceptionType type;
    private final ExceptionSeverity severity;
    private final String fieldName;
    private final String message;

    public ValidationError(
            ExceptionType type,
            ExceptionSeverity severity,
            String fieldName,
            String message
    ) {
        this.type = type;
        this.severity = severity;
        this.fieldName = fieldName;
        this.message = message;
    }

    public ExceptionType getType() {
        return type;
    }

    public ExceptionSeverity getSeverity() {
        return severity;
    }

    public String getFieldName() {
        return fieldName;
    }

    public String getMessage() {
        return message;
    }
}
