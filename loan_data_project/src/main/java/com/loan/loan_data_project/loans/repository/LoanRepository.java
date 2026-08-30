package com.loan.loan_data_project.loans.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.loan.loan_data_project.loans.entity.Loan;

public interface LoanRepository extends JpaRepository<Loan, Long>{
    boolean existsByLoanId(String loanId);
    boolean existsByLoanIdAndIdNot(String loanId, Long id);
    Loan findByLoanId(Long id);
}
