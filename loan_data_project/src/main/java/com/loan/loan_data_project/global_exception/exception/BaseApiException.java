package com.loan.loan_data_project.global_exception.exception;

import org.springframework.http.HttpStatus;

public abstract class BaseApiException extends RuntimeException {

    private final HttpStatus status;
    private final String code;

    protected BaseApiException(String message, HttpStatus status) {
        this(message, status, null, null);
    }

    protected BaseApiException(String message, HttpStatus status, Throwable cause) {
        this(message, status, null, cause);
    }

    protected BaseApiException(String message, HttpStatus status, String code) {
        this(message, status, code, null);
    }

    protected BaseApiException(String message, HttpStatus status, String code, Throwable cause) {
        super(message, cause);
        this.status = status;
        this.code = code != null ? code : defaultCode();
    }

    public HttpStatus getStatus() {
        return status;
    }

    public String getCode() {
        return code;
    }

    private String defaultCode() {
        String simpleName = getClass().getSimpleName().replaceAll("Exception$", "");
        return simpleName.replaceAll("([a-z])([A-Z])", "$1_$2").toUpperCase();
    }
}
