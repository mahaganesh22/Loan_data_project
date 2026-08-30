package com.loan.loan_data_project.verified.repository;

import com.loan.loan_data_project.verified.entity.VerifiedLoan;

import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface VerifiedLoanRepository
        extends JpaRepository<VerifiedLoan, Long> {

    Optional<VerifiedLoan> findByLoanId(String loanId);

    Optional<VerifiedLoan> findBySourceLoanId(Long sourceLoanId);

    boolean existsBySourceLoanId(Long sourceLoanId);

    Page<VerifiedLoan> findByLoanId(
            String loanId,
            Pageable pageable
    );

    Page<VerifiedLoan> findByBorrowerId(
            String borrowerId,
            Pageable pageable
    );

    Page<VerifiedLoan> findByLoanIdAndBorrowerId(
            String loanId,
            String borrowerId,
            Pageable pageable
    );
}
