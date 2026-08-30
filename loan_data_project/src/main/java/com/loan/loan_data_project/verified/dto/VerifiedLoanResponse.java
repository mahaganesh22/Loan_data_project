package com.loan.loan_data_project.verified.dto;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

import com.loan.loan_data_project.verified.entity.VerifiedLoan;

public class VerifiedLoanResponse {

    private Long id;

    private Long sourceLoanId;

    private String loanId;

    private String borrowerId;

    private String loanType;

    private LocalDate originationDate;

    private LocalDate maturityDate;

    private BigDecimal originalPrincipal;

    private BigDecimal currentBalance;

    private BigDecimal interestRate;

    private Integer termMonths;

    private String borrowerState;

    private String loanPurpose;

    private String creditGrade;

    private Integer employmentLength;

    private String incomeBand;

    private String paymentStatus;

    private Integer daysPastDue;

    private String servicerName;

    private LocalDate lastPaymentDate;

    private String documentStatus;

    private String sourceSystem;

    private String status;

    private String verifiedBy;

    private LocalDateTime verifiedAt;

    private String recordHash;

    private String aiRecommendation;


    public VerifiedLoanResponse(VerifiedLoan loan) {

        this.id = loan.getId();

        this.sourceLoanId =
                loan.getSourceLoanId();

        this.loanId =
                loan.getLoanId();

        this.borrowerId =
                loan.getBorrowerId();

        this.loanType =
                loan.getLoanType();

        this.originationDate =
                loan.getOriginationDate();

        this.maturityDate =
                loan.getMaturityDate();

        this.originalPrincipal =
                loan.getOriginalPrincipal();

        this.currentBalance =
                loan.getCurrentBalance();

        this.interestRate =
                loan.getInterestRate();

        this.termMonths =
                loan.getTermMonths();

        this.borrowerState =
                loan.getBorrowerState();

        this.loanPurpose =
                loan.getLoanPurpose();

        this.creditGrade =
                loan.getCreditGrade();

        this.employmentLength =
                loan.getEmploymentLength();

        this.incomeBand =
                loan.getIncomeBand();

        this.paymentStatus =
                loan.getPaymentStatus();

        this.daysPastDue =
                loan.getDaysPastDue();

        this.servicerName =
                loan.getServicerName();

        this.lastPaymentDate =
                loan.getLastPaymentDate();

        this.documentStatus =
                loan.getDocumentStatus();

        this.sourceSystem =
                loan.getSourceSystem();

        this.status =
                loan.getStatus().name();

        if (loan.getVerifiedBy() != null) {

            this.verifiedBy =
                    loan.getVerifiedBy().getUsername();
        }

        this.verifiedAt =
                loan.getVerifiedAt();

        this.recordHash =
                loan.getRecordHash();

        this.aiRecommendation =
                loan.getAiRecommendation();
    }


    public Long getId() {
        return id;
    }

    public Long getSourceLoanId() {
        return sourceLoanId;
    }

    public String getLoanId() {
        return loanId;
    }

    public String getBorrowerId() {
        return borrowerId;
    }

    public String getLoanType() {
        return loanType;
    }

    public LocalDate getOriginationDate() {
        return originationDate;
    }

    public LocalDate getMaturityDate() {
        return maturityDate;
    }

    public BigDecimal getOriginalPrincipal() {
        return originalPrincipal;
    }

    public BigDecimal getCurrentBalance() {
        return currentBalance;
    }

    public BigDecimal getInterestRate() {
        return interestRate;
    }

    public Integer getTermMonths() {
        return termMonths;
    }

    public String getBorrowerState() {
        return borrowerState;
    }

    public String getLoanPurpose() {
        return loanPurpose;
    }

    public String getCreditGrade() {
        return creditGrade;
    }

    public Integer getEmploymentLength() {
        return employmentLength;
    }

    public String getIncomeBand() {
        return incomeBand;
    }

    public String getPaymentStatus() {
        return paymentStatus;
    }

    public Integer getDaysPastDue() {
        return daysPastDue;
    }

    public String getServicerName() {
        return servicerName;
    }

    public LocalDate getLastPaymentDate() {
        return lastPaymentDate;
    }

    public String getDocumentStatus() {
        return documentStatus;
    }

    public String getSourceSystem() {
        return sourceSystem;
    }

    public String getStatus() {
        return status;
    }

    public String getVerifiedBy() {
        return verifiedBy;
    }

    public LocalDateTime getVerifiedAt() {
        return verifiedAt;
    }

    public String getRecordHash() {
        return recordHash;
    }

    public String getAiRecommendation() {
        return aiRecommendation;
    }
}
