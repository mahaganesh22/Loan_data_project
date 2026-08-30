package com.loan.loan_data_project.validation.service;

import org.springframework.stereotype.Service;

import com.loan.loan_data_project.exception.enums.ExceptionSeverity;
import com.loan.loan_data_project.exception.enums.ExceptionType;
import com.loan.loan_data_project.loans.entity.Loan;
import com.loan.loan_data_project.loans.repository.LoanRepository;
import com.loan.loan_data_project.validation.dto.ValidationError;
import com.loan.loan_data_project.validation.dto.ValidationResult;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Service
public class LoanValidationService {

    private final LoanRepository loanRepository;

    public LoanValidationService(LoanRepository loanRepository) {
        this.loanRepository = loanRepository;
    }

    public ValidationResult validate(Loan loan) {

        List<ValidationError> errors = new ArrayList<>();

        // Loan ID validation
        if (loan.getLoanId() == null ||
                loan.getLoanId().isBlank()) {

            errors.add(
                new ValidationError(
                        ExceptionType.MISSING_REQUIRED_FIELD,
                        ExceptionSeverity.HIGH,
                        "loan_id",
                        "Loan ID is required"
                )
            );
        }

        // Borrower ID validation
        if (loan.getBorrowerId() == null ||
                loan.getBorrowerId().isBlank()) {

            errors.add(
                new ValidationError(
                        ExceptionType.MISSING_REQUIRED_FIELD,
                        ExceptionSeverity.MEDIUM,
                        "borrower_id",
                        "Borrower ID is required"
                )
            );
        }

        // Original principal validation
        if (loan.getOriginalPrincipal() == null) {

            errors.add(
                new ValidationError(
                        ExceptionType.MISSING_REQUIRED_FIELD,
                        ExceptionSeverity.HIGH,
                        "original_principal",
                        "Original principal is required"
                )
            );

        } else if (loan.getOriginalPrincipal()
                .compareTo(BigDecimal.ZERO) < 0) {

            errors.add(
                new ValidationError(
                        ExceptionType.NEGATIVE_VALUE,
                        ExceptionSeverity.HIGH,
                        "original_principal",
                        "Original principal cannot be negative"
                )
            );
        }

        // Current balance validation
        if (loan.getCurrentBalance() == null) {

            errors.add(
                 new ValidationError(
                        ExceptionType.MISSING_REQUIRED_FIELD,
                        ExceptionSeverity.HIGH,
                        "current_balance",
                        "Current balance is required"
                    )
            );

        } else if (loan.getCurrentBalance()
                .compareTo(BigDecimal.ZERO) < 0) {

            errors.add(
                new ValidationError(
                        ExceptionType.NEGATIVE_VALUE,
                        ExceptionSeverity.HIGH,
                        "current_balance",
                        "Current balance cannot be negative"
                )
            );
        }

        // Current balance > original principal
        if (loan.getOriginalPrincipal() != null &&
                loan.getCurrentBalance() != null &&
                loan.getCurrentBalance()
                        .compareTo(loan.getOriginalPrincipal()) > 0) {

            errors.add(
                new ValidationError(
                        ExceptionType.BALANCE_EXCEEDS_PRINCIPAL,
                        ExceptionSeverity.HIGH,
                        "current_balance",
                        "Current balance cannot exceed original principal"
                )
            );
        }

        // Origination date validation
        if (loan.getOriginationDate() == null) {

            errors.add(
                new ValidationError(
                        ExceptionType.MISSING_REQUIRED_FIELD,
                        ExceptionSeverity.HIGH,
                        "origination_date",
                        "Origination date is required"
                )
            );
        }

        // Maturity date validation
        if (loan.getMaturityDate() == null) {

            errors.add(
                new ValidationError(
                        ExceptionType.MISSING_REQUIRED_FIELD,
                        ExceptionSeverity.HIGH,
                        "maturity_date",
                        "Maturity date is required"
                )
            );

        } else if (loan.getOriginationDate() != null &&
                loan.getMaturityDate()
                        .isBefore(loan.getOriginationDate())) {

            errors.add(
                new ValidationError(
                        ExceptionType.INVALID_DATE,
                        ExceptionSeverity.MEDIUM,
                        "maturity_date",
                        "Maturity date cannot be before origination date"
                )
            );
        }

        // Interest rate validation
        if (loan.getInterestRate() == null) {

            errors.add(
                new ValidationError(
                        ExceptionType.MISSING_REQUIRED_FIELD,
                        ExceptionSeverity.MEDIUM,
                        "interest_rate",
                        "Interest rate is required"
                )
            );

        } else if (loan.getInterestRate()
                .compareTo(BigDecimal.ZERO) < 0) {

            errors.add(
                new ValidationError(
                        ExceptionType.INVALID_INTEREST_RATE,
                        ExceptionSeverity.HIGH,
                        "interest_rate",
                        "Interest rate cannot be negative"
                )
            );
        }

        // Term validation
        if (loan.getTermMonths() == null) {

            errors.add(
                 new ValidationError(
                        ExceptionType.MISSING_REQUIRED_FIELD,
                        ExceptionSeverity.MEDIUM,
                        "term_months",
                        "Term months is required"
                )
            );

        } else if (loan.getTermMonths() <= 0) {

            errors.add(
                new ValidationError(
                        ExceptionType.INVALID_TERM,
                        ExceptionSeverity.MEDIUM,
                        "term_months",
                        "Term months must be greater than zero"
                )

            );
        }

        // Duplicate loan ID
        if (loan.getLoanId() != null && !loan.getLoanId().isBlank()) {
            boolean duplicate;

            if (loan.getId() == null) {

                // New loan
                duplicate =
                        loanRepository.existsByLoanId(
                                loan.getLoanId()
                        );

            } else {

                // Existing loan being revalidated
                duplicate =
                        loanRepository.existsByLoanIdAndIdNot(
                                loan.getLoanId(),
                                loan.getId()
                        );
            }

            if (duplicate) {

                errors.add(
                    new ValidationError(
                            ExceptionType.DUPLICATE_LOAN_ID,
                            ExceptionSeverity.HIGH,
                            "loan_id",
                            "Loan ID already exists: "
                                    + loan.getLoanId()
                    )
                );
            }
        }

        return new ValidationResult(
                errors.isEmpty(),
                errors
        );
    }

    public boolean isExceptionResolved(Loan loan, ExceptionType exceptionType, String fieldName) {

        ValidationResult validationResult = validate(loan);

        for (ValidationError error : validationResult.getErrors()) {

            if (error.getType() == exceptionType
                    && (fieldName == null || fieldName.equals(error.getFieldName()))) {
                return false;
            }
        }

        return true;
    }
}
