package com.loan.loan_data_project.verified.service;

import org.springframework.stereotype.Service;

// import com.loan.loan_data_project.audits.enums.AuditAction;
// import com.loan.loan_data_project.audits.service.AuditService;
import com.loan.loan_data_project.verified.entity.VerifiedLoan;
import com.loan.loan_data_project.verified.repository.VerifiedLoanRepository;

import java.util.List;

@Service
public class VerifiedLoanExportService {

    private final VerifiedLoanRepository verifiedLoanRepository;
//     private final AuditService auditService;

    public VerifiedLoanExportService(
            VerifiedLoanRepository verifiedLoanRepository
    ) {
        this.verifiedLoanRepository = verifiedLoanRepository;
        // this.auditService = auditService;
    }

    public String generateCsv() {

        List<VerifiedLoan> loans =
                verifiedLoanRepository.findAll();

        StringBuilder csv = new StringBuilder();

        // Header
        csv.append(
                "id,"
                        + "source_loan_id,"
                        + "loan_id,"
                        + "borrower_id,"
                        + "loan_type,"
                        + "origination_date,"
                        + "maturity_date,"
                        + "original_principal,"
                        + "current_balance,"
                        + "interest_rate,"
                        + "term_months,"
                        + "borrower_state,"
                        + "loan_purpose,"
                        + "credit_grade,"
                        + "employment_length,"
                        + "income_band,"
                        + "payment_status,"
                        + "days_past_due,"
                        + "servicer_name,"
                        + "last_payment_date,"
                        + "document_status,"
                        + "source_system,"
                        + "verified_by,"
                        + "status,"
                        + "verified_at,"
                        + "record_hash"
                        + "\n"
        );

        // Data
        for (VerifiedLoan loan : loans) {

            csv.append(
                    value(loan.getId())
            ).append(",");

            csv.append(
                    value(loan.getSourceLoanId())
            ).append(",");

            csv.append(
                    value(loan.getLoanId())
            ).append(",");

            csv.append(
                    value(loan.getBorrowerId())
            ).append(",");

            csv.append(
                    value(loan.getLoanType())
            ).append(",");

            csv.append(
                    value(loan.getOriginationDate())
            ).append(",");

            csv.append(
                    value(loan.getMaturityDate())
            ).append(",");

            csv.append(
                    value(loan.getOriginalPrincipal())
            ).append(",");

            csv.append(
                    value(loan.getCurrentBalance())
            ).append(",");

            csv.append(
                    value(loan.getInterestRate())
            ).append(",");

            csv.append(
                    value(loan.getTermMonths())
            ).append(",");

            csv.append(
                    value(loan.getBorrowerState())
            ).append(",");

            csv.append(
                    value(loan.getLoanPurpose())
            ).append(",");

            csv.append(
                    value(loan.getCreditGrade())
            ).append(",");

            csv.append(
                    value(loan.getEmploymentLength())
            ).append(",");

            csv.append(
                    value(loan.getIncomeBand())
            ).append(",");

            csv.append(
                    value(loan.getPaymentStatus())
            ).append(",");

            csv.append(
                    value(loan.getDaysPastDue())
            ).append(",");

            csv.append(
                    value(loan.getServicerName())
            ).append(",");

            csv.append(
                    value(loan.getLastPaymentDate())
            ).append(",");

            csv.append(
                    value(loan.getDocumentStatus())
            ).append(",");

            csv.append(
                    value(loan.getSourceSystem())
            ).append(",");

            String username = "";

            if (loan.getVerifiedBy() != null) {
                username =
                        loan.getVerifiedBy().getUsername();
            }

            csv.append(
                    value(username)
            ).append(",");

            csv.append(
                    value(loan.getStatus())
            ).append(",");

            csv.append(
                    value(loan.getVerifiedAt())
            ).append(",");

            csv.append(
                    value(loan.getRecordHash())
            ).append("\n");
        }

        // auditService.log(AuditAction.VERIFIED_RECORDS_EXPORTED, "VERIFIED_LOAN", null, null, null);

        return csv.toString();
    }

    private String value(Object value) {

        if (value == null) {
            return "";
        }

        String text = value.toString();

        /*
         * Escape values containing:
         * comma, quote, or newline.
         */
        if (text.contains(",")
                || text.contains("\"")
                || text.contains("\n")) {

            text = text.replace(
                    "\"",
                    "\"\""
            );

            return "\"" + text + "\"";
        }

        return text;
    }
}
