package com.loan.loan_data_project.audits.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.loan.loan_data_project.audits.dto.AuditLogResponse;
import com.loan.loan_data_project.audits.repository.AuditLogRepository;

import java.util.List;

@RestController
@RequestMapping("/api/audits")
public class AuditController {

    private final AuditLogRepository auditLogRepository;

    public AuditController(
            AuditLogRepository auditLogRepository
    ) {
        this.auditLogRepository = auditLogRepository;
    }

    @GetMapping("/loan/{loanId}")
    public ResponseEntity<List<AuditLogResponse>> getLoanAuditHistory(
            @PathVariable Long loanId
    ) {

        List<AuditLogResponse> response = auditLogRepository
                .findByEntityTypeAndEntityId("LOAN", loanId)
                .stream()
                .map(AuditLogResponse::new)
                .toList();

        return ResponseEntity.ok(response);
    }

    @GetMapping("/exception/{exceptionId}")
    public ResponseEntity<List<AuditLogResponse>> getExceptionAuditHistory(
            @PathVariable Long exceptionId
    ) {

        List<AuditLogResponse> response = auditLogRepository
                .findByEntityTypeAndEntityId("EXCEPTION", exceptionId)
                .stream()
                .map(AuditLogResponse::new)
                .toList();

        return ResponseEntity.ok(response);
    }
}
