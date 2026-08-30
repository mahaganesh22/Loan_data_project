package com.loan.loan_data_project.audits.config;

import java.util.Arrays;
import java.util.stream.Collectors;

import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

import com.loan.loan_data_project.audits.enums.AuditAction;

@Component
public class AuditActionConstraintMigrator implements ApplicationRunner {

    private final JdbcTemplate jdbcTemplate;

    public AuditActionConstraintMigrator(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public void run(ApplicationArguments args) {
        String allowed = Arrays.stream(AuditAction.values())
                .map(action -> "'" + action.name() + "'")
                .collect(Collectors.joining(", "));

        jdbcTemplate.execute("ALTER TABLE audit_logs DROP CONSTRAINT IF EXISTS audit_logs_action_check");
        jdbcTemplate.execute(
                "ALTER TABLE audit_logs ADD CONSTRAINT audit_logs_action_check CHECK (action IN (" + allowed + "))"
        );
    }
}
