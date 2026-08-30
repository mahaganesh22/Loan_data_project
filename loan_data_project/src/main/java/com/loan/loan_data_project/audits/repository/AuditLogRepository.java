package com.loan.loan_data_project.audits.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.loan.loan_data_project.audits.entity.AuditLog;
import com.loan.loan_data_project.audits.enums.AuditAction;

public interface AuditLogRepository extends JpaRepository<AuditLog, Long>{

    List<AuditLog> findByEntityTypeAndEntityId(
            String entityType,
            Long entityId
    );

    List<AuditLog> findByAction(
            AuditAction action
    );
}
