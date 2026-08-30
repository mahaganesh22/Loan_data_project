package com.loan.loan_data_project.audits.service;

import org.springframework.stereotype.Service;

import com.loan.loan_data_project.audits.entity.AuditLog;
import com.loan.loan_data_project.audits.enums.AuditAction;
import com.loan.loan_data_project.audits.repository.AuditLogRepository;
import com.loan.loan_data_project.user.entity.User;

import java.time.LocalDateTime;

@Service
public class AuditService {

    private final AuditLogRepository auditLogRepository;

    public AuditService(
            AuditLogRepository auditLogRepository
    ) {
        this.auditLogRepository = auditLogRepository;
    }

    public AuditLog log(
            AuditAction action,
            String entityType,
            Long entityId,
            User performedBy,
            String details
    ) {

        AuditLog auditLog = new AuditLog();

        auditLog.setAction(action);
        auditLog.setEntityType(entityType);
        auditLog.setEntityId(entityId);
        auditLog.setPerformedBy(performedBy);
        auditLog.setDetails(details);
        auditLog.setCreatedAt(LocalDateTime.now());

        return auditLogRepository.save(auditLog);
    }
}
