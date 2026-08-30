package com.loan.loan_data_project.exception.service;

import org.springframework.stereotype.Service;

import com.loan.loan_data_project.global_exception.exception.ExceptionAlreadyProcessedException;
import com.loan.loan_data_project.global_exception.exception.FieldNotCorrectableException;
import com.loan.loan_data_project.global_exception.exception.LoanExceptionNotFoundException;
import com.loan.loan_data_project.global_exception.exception.UnauthorizedReviewException;
import com.loan.loan_data_project.audits.enums.AuditAction;
import com.loan.loan_data_project.audits.service.AuditService;
import com.loan.loan_data_project.exception.dto.ExceptionReviewRequest;
import com.loan.loan_data_project.exception.entity.LoanException;
import com.loan.loan_data_project.exception.enums.ExceptionSeverity;
import com.loan.loan_data_project.exception.enums.ExceptionStatus;
import com.loan.loan_data_project.exception.repository.LoanExceptionRepository;
import com.loan.loan_data_project.loans.entity.Loan;
import com.loan.loan_data_project.loans.repository.LoanRepository;
import com.loan.loan_data_project.user.entity.User;
import com.loan.loan_data_project.user.enums.UserRole;
import com.loan.loan_data_project.validation.dto.ValidationError;
import com.loan.loan_data_project.validation.service.LoanValidationService;
import com.loan.loan_data_project.verified.service.AuthenticatedUserService;

import jakarta.transaction.Transactional;

import java.math.BigDecimal;
import java.time.DateTimeException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class ExceptionService {

    private final LoanExceptionRepository loanExceptionRepository;
    private final LoanRepository loanRepository;
    private final LoanValidationService loanValidationService;
    private final AuditService auditService;
    private final AuthenticatedUserService authenticatedUserService;

    public ExceptionService(
            LoanExceptionRepository loanExceptionRepository,
            AuthenticatedUserService authenticatedUserService,
            LoanRepository loanRepository,
            LoanValidationService loanValidationService,
            AuditService auditService
    ) {
        this.loanExceptionRepository = loanExceptionRepository;
        this.authenticatedUserService = authenticatedUserService;
        this.loanRepository = loanRepository;
        this.loanValidationService = loanValidationService;
        this.auditService = auditService;
    }

    public LoanException createException(Loan loan, ValidationError error) {

        LoanException exception = new LoanException();

        exception.setLoan(loan);
        exception.setExceptionType(error.getType());
        exception.setSeverity(error.getSeverity());
        exception.setFieldName(error.getFieldName());
        exception.setMessage(error.getMessage());
        exception.setStatus(ExceptionStatus.OPEN);
        exception.setCreatedAt(LocalDateTime.now());

        LoanException savedException = loanExceptionRepository.save(exception);

        auditService.log(
                AuditAction.EXCEPTION_CREATED,
    "EXCEPTION",
                savedException.getId(),
    null,
                "Exception created for loan "
                        + loan.getLoanId()
                        + ": "
                        + error.getMessage()
        );

        return savedException;
    }

    public List<LoanException> getAllExceptions() {
        return loanExceptionRepository.findAll();
    }

    public LoanException getExceptionById(Long id) {

        return loanExceptionRepository.findById(id)
                .orElseThrow(() ->
                        new LoanExceptionNotFoundException(id)
                );
    }

    public List<LoanException> getByStatus(ExceptionStatus status) {

        return loanExceptionRepository.findByStatus(status);
    }

    public List<LoanException> getBySeverity(ExceptionSeverity severity) {

        return loanExceptionRepository.findBySeverity(severity);
    }

    @Transactional
    public LoanException resolveException(Long exceptionId, ExceptionReviewRequest exceptionReviewRequest) {

        User reviewer = authenticatedUserService.getCurrentUser();

        // Long reviewerId = reviewer.getId();
        String correctedValue = trimToNull(exceptionReviewRequest.getCorrectedValue());
        String reviewerComment = trimToNull(exceptionReviewRequest.getReviewerComment());
        if (correctedValue == null) {
            throw new IllegalArgumentException("Corrected value is required");
        }
        if (reviewerComment == null) {
            throw new IllegalArgumentException("Reviewer comment is required");
        }

        LoanException exception =
                loanExceptionRepository.findById(exceptionId)
                        .orElseThrow(() ->
                                new LoanExceptionNotFoundException(exceptionId)
                        );

        if (exception.getStatus() != ExceptionStatus.OPEN) {

            throw new ExceptionAlreadyProcessedException();
        }

        if (reviewer.getRole() != UserRole.REVIEWER) {

            throw new UnauthorizedReviewException();
        }

        Loan loan = exception.getLoan();
        String fieldName = resolveCorrectableField(exception);

        String originalValue =
                getCurrentFieldValue(
                        loan,
                        fieldName
                );

        updateLoanField(
                loan,
                fieldName,
                correctedValue
        );

        loanRepository.save(loan);

        auditService.log(
                AuditAction.FIELD_CORRECTED,
                "LOAN",
                loan.getId(),
                reviewer,
                exception.getFieldName()
                        + " changed from "
                        + originalValue
                        + " to "
                        + correctedValue
        );

        boolean exceptionResolved = loanValidationService.isExceptionResolved(
                loan,
                exception.getExceptionType(),
                fieldName
        );

        exception.setOriginalValue(originalValue);
        exception.setCorrectedValue(correctedValue);
        exception.setReviewerComment(reviewerComment);
        exception.setReviewedBy(reviewer);
        exception.setReviewedAt(LocalDateTime.now());
        if (exceptionReviewRequest.getAiDecision() != null && !exceptionReviewRequest.getAiDecision().isBlank()) {
            exception.setAiDecision(exceptionReviewRequest.getAiDecision().trim().toUpperCase());
        }

        if (exceptionResolved) {

            exception.setStatus(ExceptionStatus.RESOLVED);
            auditService.log(
                AuditAction.EXCEPTION_RESOLVED,
                "EXCEPTION",
                exception.getId(),
                reviewer,
                "Exception resolved after reviewer correction"
            );
        } else {

            exception.setStatus(ExceptionStatus.OPEN);
            // exception.setReviewerComment(reviewerComment);
        }

        loanExceptionRepository.save(exception);

        return exception;
    }

    private String resolveCorrectableField(LoanException exception) {
        String fieldName = exception.getFieldName();
        String message = exception.getMessage() == null ? "" : exception.getMessage().toLowerCase();
        Loan loan = exception.getLoan();
        if ("maturity_date".equals(fieldName)
                && loan.getOriginationDate() == null
                && message.contains("maturity date is required")
                && loan.getMaturityDate() != null) {
            return "origination_date";
        }
        return fieldName;
    }

    private String getCurrentFieldValue(Loan loan, String fieldName) {

        return switch (fieldName) {

            case "original_principal" ->
                    loan.getOriginalPrincipal() == null
                            ? null
                            : loan.getOriginalPrincipal().toString();

            case "current_balance" ->
                    loan.getCurrentBalance() == null
                            ? null
                            : loan.getCurrentBalance().toString();

            case "interest_rate" ->
                    loan.getInterestRate() == null
                            ? null
                            : loan.getInterestRate().toString();

            case "term_months" ->
                    loan.getTermMonths() == null
                            ? null
                            : loan.getTermMonths().toString();

            case "loan_id" ->
                    loan.getLoanId();

            case "borrower_id" ->
                    loan.getBorrowerId();

            case "maturity_date" ->
                    loan.getMaturityDate() == null
                            ? null
                            : loan.getMaturityDate().toString();

            case "origination_date" ->
                    loan.getOriginationDate() == null
                            ? null
                            : loan.getOriginationDate().toString();

            case "borrower_state" ->
                    loan.getBorrowerState();

            case "payment_status" ->
                    loan.getPaymentStatus();

            case "days_past_due" ->
                    loan.getDaysPastDue() == null
                            ? null
                            : loan.getDaysPastDue().toString();

            case "document_status" ->
                    loan.getDocumentStatus();

            case "loan_type" ->
                    loan.getLoanType();

            default ->
                    throw new FieldNotCorrectableException(fieldName);
        };
    }

    private void updateLoanField(Loan loan, String fieldName, String correctedValue) {

        try {
            switch (fieldName) {

                case "original_principal" ->
                        loan.setOriginalPrincipal(parseDecimal(correctedValue));

                case "current_balance" ->
                        loan.setCurrentBalance(parseDecimal(correctedValue));

                case "interest_rate" ->
                        loan.setInterestRate(parseDecimal(correctedValue));

                case "term_months" ->
                        loan.setTermMonths(parseInteger(correctedValue));

                case "loan_id" ->
                        loan.setLoanId(correctedValue);

                case "borrower_id" ->
                        loan.setBorrowerId(correctedValue);

                case "maturity_date" ->
                        loan.setMaturityDate(parseDate(correctedValue));

                case "origination_date" ->
                        loan.setOriginationDate(parseDate(correctedValue));

                case "borrower_state" ->
                        loan.setBorrowerState(correctedValue);

                case "payment_status" ->
                        loan.setPaymentStatus(correctedValue);

                case "days_past_due" ->
                        loan.setDaysPastDue(parseInteger(correctedValue));

                case "document_status" ->
                        loan.setDocumentStatus(correctedValue);

                case "loan_type" ->
                        loan.setLoanType(correctedValue);

                default ->
                        throw new FieldNotCorrectableException(fieldName);
            }
        } catch (NumberFormatException | DateTimeException ex) {
            throw new IllegalArgumentException(
                    "Corrected value '" + correctedValue + "' is not valid for field " + fieldName,
                    ex
            );
        }

        loanRepository.save(loan);
    }

    private String trimToNull(String value) {
        if (value == null) {
            return null;
        }
        String trimmed = value.trim();
        return trimmed.isEmpty() ? null : trimmed;
    }

    private BigDecimal parseDecimal(String value) {
        String normalized = value.replace(",", "").replace("%", "").replace("$", "").trim();
        return new BigDecimal(normalized);
    }

    private Integer parseInteger(String value) {
        return Integer.parseInt(value.replace(",", "").trim());
    }

    private LocalDate parseDate(String value) {
        String trimmed = value.trim();
        if (trimmed.length() >= 10) {
            return LocalDate.parse(trimmed.substring(0, 10));
        }
        return LocalDate.parse(trimmed);
    }
}
