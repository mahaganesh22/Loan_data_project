package com.loan.loan_data_project.audits.dto;

import java.time.LocalDateTime;

import com.loan.loan_data_project.audits.entity.AuditLog;
import com.loan.loan_data_project.audits.enums.AuditAction;

public class AuditLogResponse {

    private Long id;
    private AuditAction action;
    private String entityType;
    private Long entityId;
    private String performedBy;
    private String details;
    private LocalDateTime createdAt;

    public AuditLogResponse(AuditLog auditLog) {
        this.id = auditLog.getId();
        this.action = auditLog.getAction();
        this.entityType = auditLog.getEntityType();
        this.entityId = auditLog.getEntityId();
        this.performedBy = auditLog.getPerformedBy() == null
                ? null
                : auditLog.getPerformedBy().getUsername();
        this.details = auditLog.getDetails();
        this.createdAt = auditLog.getCreatedAt();
    }

    public Long getId() {
        return id;
    }

    public AuditAction getAction() {
        return action;
    }

    public String getEntityType() {
        return entityType;
    }

    public Long getEntityId() {
        return entityId;
    }

    public String getPerformedBy() {
        return performedBy;
    }

    public String getDetails() {
        return details;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
}
