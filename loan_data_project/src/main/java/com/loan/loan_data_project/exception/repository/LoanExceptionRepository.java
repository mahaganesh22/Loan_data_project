package com.loan.loan_data_project.exception.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.loan.loan_data_project.exception.entity.LoanException;
import com.loan.loan_data_project.exception.enums.ExceptionSeverity;
import com.loan.loan_data_project.exception.enums.ExceptionStatus;
import java.util.List;


public interface LoanExceptionRepository extends JpaRepository<LoanException, Long>{
    List<LoanException> findByStatus(ExceptionStatus status);
    List<LoanException> findByLoan_Id(Long loanPk);
    List<LoanException> findBySeverity(ExceptionSeverity severity);
    
}