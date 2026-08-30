package com.loan.loan_data_project.verified.entity;

import jakarta.persistence.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

import com.loan.loan_data_project.user.entity.User;
import com.loan.loan_data_project.verified.enums.VerificationStatus;

@Entity
@Table(name = "verified_loans")
public class VerifiedLoan {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /*
     * Reference to the original loan from which
     * this verified snapshot was created.
     */
    @Column(nullable = false)
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

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "verified_by")
    private User verifiedBy;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private VerificationStatus status;

    @Column(nullable = false)
    private LocalDateTime verifiedAt;

    @Column(nullable = false, unique = true)
    private String recordHash;

    @Column(length = 2000)
    private String aiRecommendation;


    public Long getId() {
        return id;
    }

    public Long getSourceLoanId() {
        return sourceLoanId;
    }

    public void setSourceLoanId(Long sourceLoanId) {
        this.sourceLoanId = sourceLoanId;
    }

    public String getLoanId() {
        return loanId;
    }

    public void setLoanId(String loanId) {
        this.loanId = loanId;
    }

    public String getBorrowerId() {
        return borrowerId;
    }

    public void setBorrowerId(String borrowerId) {
        this.borrowerId = borrowerId;
    }

    public String getLoanType() {
        return loanType;
    }

    public void setLoanType(String loanType) {
        this.loanType = loanType;
    }

    public LocalDate getOriginationDate() {
        return originationDate;
    }

    public void setOriginationDate(LocalDate originationDate) {
        this.originationDate = originationDate;
    }

    public LocalDate getMaturityDate() {
        return maturityDate;
    }

    public void setMaturityDate(LocalDate maturityDate) {
        this.maturityDate = maturityDate;
    }

    public BigDecimal getOriginalPrincipal() {
        return originalPrincipal;
    }

    public void setOriginalPrincipal(BigDecimal originalPrincipal) {
        this.originalPrincipal = originalPrincipal;
    }

    public BigDecimal getCurrentBalance() {
        return currentBalance;
    }

    public void setCurrentBalance(BigDecimal currentBalance) {
        this.currentBalance = currentBalance;
    }

    public BigDecimal getInterestRate() {
        return interestRate;
    }

    public void setInterestRate(BigDecimal interestRate) {
        this.interestRate = interestRate;
    }

    public Integer getTermMonths() {
        return termMonths;
    }

    public void setTermMonths(Integer termMonths) {
        this.termMonths = termMonths;
    }

    public String getBorrowerState() {
        return borrowerState;
    }

    public void setBorrowerState(String borrowerState) {
        this.borrowerState = borrowerState;
    }

    public String getLoanPurpose() {
        return loanPurpose;
    }

    public void setLoanPurpose(String loanPurpose) {
        this.loanPurpose = loanPurpose;
    }

    public String getCreditGrade() {
        return creditGrade;
    }

    public void setCreditGrade(String creditGrade) {
        this.creditGrade = creditGrade;
    }

    public Integer getEmploymentLength() {
        return employmentLength;
    }

    public void setEmploymentLength(Integer employmentLength) {
        this.employmentLength = employmentLength;
    }

    public String getIncomeBand() {
        return incomeBand;
    }

    public void setIncomeBand(String incomeBand) {
        this.incomeBand = incomeBand;
    }

    public String getPaymentStatus() {
        return paymentStatus;
    }

    public void setPaymentStatus(String paymentStatus) {
        this.paymentStatus = paymentStatus;
    }

    public Integer getDaysPastDue() {
        return daysPastDue;
    }

    public void setDaysPastDue(Integer daysPastDue) {
        this.daysPastDue = daysPastDue;
    }

    public String getServicerName() {
        return servicerName;
    }

    public void setServicerName(String servicerName) {
        this.servicerName = servicerName;
    }

    public LocalDate getLastPaymentDate() {
        return lastPaymentDate;
    }

    public void setLastPaymentDate(LocalDate lastPaymentDate) {
        this.lastPaymentDate = lastPaymentDate;
    }

    public String getDocumentStatus() {
        return documentStatus;
    }

    public void setDocumentStatus(String documentStatus) {
        this.documentStatus = documentStatus;
    }

    public String getSourceSystem() {
        return sourceSystem;
    }

    public void setSourceSystem(String sourceSystem) {
        this.sourceSystem = sourceSystem;
    }

    public User getVerifiedBy() {
        return verifiedBy;
    }

    public void setVerifiedBy(User verifiedBy) {
        this.verifiedBy = verifiedBy;
    }

    public VerificationStatus getStatus() {
        return status;
    }

    public void setStatus(VerificationStatus status) {
        this.status = status;
    }

    public LocalDateTime getVerifiedAt() {
        return verifiedAt;
    }

    public void setVerifiedAt(LocalDateTime verifiedAt) {
        this.verifiedAt = verifiedAt;
    }

    public String getRecordHash() {
        return recordHash;
    }

    public void setRecordHash(String recordHash) {
        this.recordHash = recordHash;
    }

    public String getAiRecommendation() {
        return aiRecommendation;
    }

    public void setAiRecommendation(String aiRecommendation) {
        this.aiRecommendation = aiRecommendation;
    }
}