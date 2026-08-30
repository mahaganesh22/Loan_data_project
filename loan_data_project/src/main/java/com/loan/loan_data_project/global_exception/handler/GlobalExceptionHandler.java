package com.loan.loan_data_project.global_exception.handler;

import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.InsufficientAuthenticationException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.validation.FieldError;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.multipart.MaxUploadSizeExceededException;
import org.springframework.web.servlet.resource.NoResourceFoundException;

import com.loan.loan_data_project.global_exception.dto.ErrorResponse;
import com.loan.loan_data_project.global_exception.dto.ErrorResponses;
import com.loan.loan_data_project.global_exception.exception.AccessForbiddenException;
import com.loan.loan_data_project.global_exception.exception.AccountDisabledException;
import com.loan.loan_data_project.global_exception.exception.BaseApiException;
import com.loan.loan_data_project.global_exception.exception.InvalidCredentialsException;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.ConstraintViolationException;

@RestControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger log = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    @ExceptionHandler(BaseApiException.class)
    public ResponseEntity<ErrorResponse> handleBaseApiException(
            BaseApiException ex,
            HttpServletRequest request
    ) {
        return respond(ex.getStatus(), ex.getMessage(), request.getRequestURI(), ex.getCode());
    }

    @ExceptionHandler({BadCredentialsException.class, UsernameNotFoundException.class})
    public ResponseEntity<ErrorResponse> handleBadCredentials(HttpServletRequest request) {
        InvalidCredentialsException ex = new InvalidCredentialsException();
        return respond(ex.getStatus(), ex.getMessage(), request.getRequestURI(), ex.getCode());
    }

    @ExceptionHandler(DisabledException.class)
    public ResponseEntity<ErrorResponse> handleDisabledAccount(HttpServletRequest request) {
        AccountDisabledException ex = new AccountDisabledException();
        return respond(ex.getStatus(), ex.getMessage(), request.getRequestURI(), ex.getCode());
    }

    @ExceptionHandler({InsufficientAuthenticationException.class, AuthenticationException.class})
    public ResponseEntity<ErrorResponse> handleAuthenticationException(
            AuthenticationException ex,
            HttpServletRequest request
    ) {
        if (ex instanceof BadCredentialsException || ex instanceof UsernameNotFoundException) {
            return handleBadCredentials(request);
        }
        if (ex instanceof DisabledException) {
            return handleDisabledAccount(request);
        }
        return respond(
                HttpStatus.UNAUTHORIZED,
                "Authentication failed. Please sign in again.",
                request.getRequestURI(),
                "AUTHENTICATION_FAILED"
        );
    }

    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<ErrorResponse> handleAccessDenied(HttpServletRequest request) {
        AccessForbiddenException ex = new AccessForbiddenException();
        return respond(ex.getStatus(), ex.getMessage(), request.getRequestURI(), ex.getCode());
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleValidation(
            MethodArgumentNotValidException ex,
            HttpServletRequest request
    ) {
        String message = ex.getBindingResult().getFieldErrors().stream()
                .map(this::formatFieldError)
                .collect(Collectors.joining(" "));
        if (message.isBlank()) {
            message = "Request validation failed. Check the submitted fields and try again.";
        }
        return respond(HttpStatus.BAD_REQUEST, message, request.getRequestURI(), "VALIDATION_ERROR");
    }

    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<ErrorResponse> handleConstraintViolation(
            ConstraintViolationException ex,
            HttpServletRequest request
    ) {
        String message = ex.getConstraintViolations().stream()
                .map(violation -> violation.getMessage())
                .collect(Collectors.joining(" "));
        return respond(HttpStatus.BAD_REQUEST, message, request.getRequestURI(), "VALIDATION_ERROR");
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ErrorResponse> handleUnreadableBody(HttpServletRequest request) {
        return respond(
                HttpStatus.BAD_REQUEST,
                "Request body is missing or is not valid JSON.",
                request.getRequestURI(),
                "INVALID_REQUEST_BODY"
        );
    }

    @ExceptionHandler(MissingServletRequestParameterException.class)
    public ResponseEntity<ErrorResponse> handleMissingParameter(
            MissingServletRequestParameterException ex,
            HttpServletRequest request
    ) {
        return respond(
                HttpStatus.BAD_REQUEST,
                "Required parameter '" + ex.getParameterName() + "' is missing.",
                request.getRequestURI(),
                "MISSING_PARAMETER"
        );
    }

    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    public ResponseEntity<ErrorResponse> handleMethodNotSupported(
            HttpRequestMethodNotSupportedException ex,
            HttpServletRequest request
    ) {
        return respond(
                HttpStatus.METHOD_NOT_ALLOWED,
                "HTTP method " + ex.getMethod() + " is not supported for this endpoint.",
                request.getRequestURI(),
                "METHOD_NOT_ALLOWED"
        );
    }

    @ExceptionHandler(NoResourceFoundException.class)
    public ResponseEntity<ErrorResponse> handleNotFound(
            NoResourceFoundException ex,
            HttpServletRequest request
    ) {
        return respond(
                HttpStatus.NOT_FOUND,
                "No API endpoint found for " + request.getRequestURI() + ".",
                request.getRequestURI(),
                "NOT_FOUND"
        );
    }

    @ExceptionHandler(MaxUploadSizeExceededException.class)
    public ResponseEntity<ErrorResponse> handleUploadTooLarge(HttpServletRequest request) {
        return respond(
                HttpStatus.CONTENT_TOO_LARGE,
                "The uploaded file is too large. Use a smaller CSV loan tape and try again.",
                request.getRequestURI(),
                "UPLOAD_TOO_LARGE"
        );
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ErrorResponse> handleIllegalArgumentException(
            IllegalArgumentException ex,
            HttpServletRequest request
    ) {
        return respond(HttpStatus.BAD_REQUEST, ex.getMessage(), request.getRequestURI(), "INVALID_ARGUMENT");
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleGenericException(
            Exception ex,
            HttpServletRequest request
    ) {
        log.error("Unhandled error on {}", request.getRequestURI(), ex);
        return respond(
                HttpStatus.INTERNAL_SERVER_ERROR,
                "An unexpected error occurred. Please try again, or contact support if it continues.",
                request.getRequestURI(),
                "INTERNAL_ERROR"
        );
    }

    private String formatFieldError(FieldError error) {
        String field = error.getField();
        String defaultMessage = error.getDefaultMessage();
        if (defaultMessage == null || defaultMessage.isBlank()) {
            return field + " is invalid.";
        }
        if (defaultMessage.toLowerCase().contains(field.toLowerCase())) {
            return defaultMessage.endsWith(".") ? defaultMessage : defaultMessage + ".";
        }
        return field + ": " + defaultMessage + (defaultMessage.endsWith(".") ? "" : ".");
    }

    private ResponseEntity<ErrorResponse> respond(HttpStatus status, String message, String path, String code) {
        return ResponseEntity.status(status).body(ErrorResponses.of(status, message, path, code));
    }
}
