package com.loan.loan_data_project.exception.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.loan.loan_data_project.exception.dto.ExceptionResponse;
import com.loan.loan_data_project.exception.dto.ExceptionReviewRequest;
import com.loan.loan_data_project.exception.entity.LoanException;
import com.loan.loan_data_project.exception.enums.ExceptionSeverity;
import com.loan.loan_data_project.exception.enums.ExceptionStatus;
import com.loan.loan_data_project.exception.service.ExceptionService;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/exceptions")
public class ExceptionController {

    private final ExceptionService exceptionService;

    public ExceptionController(ExceptionService exceptionService) {
        this.exceptionService = exceptionService;
    }

    @GetMapping
    public ResponseEntity<List<ExceptionResponse>> getExceptions(
            @RequestParam(required = false) ExceptionStatus status,
            @RequestParam(required = false) ExceptionSeverity severity
    ) {

        List<LoanException> exceptions;

        if (status != null) {

            exceptions = exceptionService.getByStatus(status);

        } else if (severity != null) {

            exceptions = exceptionService.getBySeverity(severity);

        } else {

            exceptions = exceptionService.getAllExceptions();
        }

        List<ExceptionResponse> response = exceptions.stream()
                .map(ExceptionResponse::new)
                .toList();

        return ResponseEntity.ok(response);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ExceptionResponse> getException(
            @PathVariable Long id
    ) {

        LoanException exception =
                exceptionService.getExceptionById(id);

        return ResponseEntity.ok(
                new ExceptionResponse(exception)
        );
    }

    @PatchMapping("/{exceptionId}")
    public ResponseEntity<ExceptionResponse> resolveException(
                                            @PathVariable Long exceptionId, 
                                            @RequestBody ExceptionReviewRequest exceptionReviewRequest
                                        ) {
        LoanException exception = exceptionService.resolveException(exceptionId, exceptionReviewRequest);

        return ResponseEntity.ok(new ExceptionResponse(exception));
    }
}
