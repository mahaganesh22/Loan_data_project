package com.loan.loan_data_project.exception.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

import com.loan.loan_data_project.exception.enums.ExceptionSeverity;
import com.loan.loan_data_project.exception.enums.ExceptionStatus;
import com.loan.loan_data_project.exception.enums.ExceptionType;
import com.loan.loan_data_project.loans.entity.Loan;
import com.loan.loan_data_project.user.entity.User;

@Entity
@Table(name = "exceptions")
public class LoanException {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "loan_id", nullable = false)
    private Loan loan;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ExceptionType exceptionType;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ExceptionSeverity severity;

    private String fieldName;

    @Column(nullable = false, length = 1000)
    private String message;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ExceptionStatus status;

    @Column(nullable = false)
    private LocalDateTime createdAt;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "reviewed_by")
    private User reviewedBy;

    private LocalDateTime reviewedAt;

    private String originalValue;

    private String correctedValue;

    private String reviewerComment;

    private String aiModel;

    @Column(length = 2000)
    private String aiPrompt;

    @Column(length = 2000)
    private String aiExplanation;

    private String aiSuggestedCorrection;

    @Column(length = 2000)
    private String aiReviewerNote;

    @Column(length = 1000)
    private String aiSeverityRationale;

    private String aiClassifiedSeverity;

    @Column(length = 2000)
    private String aiConflictComparison;

    private LocalDateTime aiGeneratedAt;

    private String aiDecision;

    public Long getId() {
        return id;
    }

    public Loan getLoan() {
        return loan;
    }

    public void setLoan(Loan loan) {
        this.loan = loan;
    }

    public ExceptionType getExceptionType() {
        return exceptionType;
    }

    public void setExceptionType(ExceptionType exceptionType) {
        this.exceptionType = exceptionType;
    }

    public ExceptionSeverity getSeverity() {
        return severity;
    }

    public void setSeverity(ExceptionSeverity severity) {
        this.severity = severity;
    }

    public String getFieldName() {
        return fieldName;
    }

    public void setFieldName(String fieldName) {
        this.fieldName = fieldName;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public ExceptionStatus getStatus() {
        return status;
    }

    public void setStatus(ExceptionStatus status) {
        this.status = status;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public User getReviewedBy() {
        return reviewedBy;
    }

    public void setReviewedBy(User reviewedBy) {
        this.reviewedBy = reviewedBy;
    }

    public LocalDateTime getReviewedAt() {
        return reviewedAt;
    }

    public void setReviewedAt(LocalDateTime reviewedAt) {
        this.reviewedAt = reviewedAt;
    }

    public String getOriginalValue() {
        return originalValue;
    }

    public void setOriginalValue(String originalValue) {
        this.originalValue = originalValue;
    }

    public String getCorrectedValue() {
        return correctedValue;
    }

    public void setCorrectedValue(String correctedValue) {
        this.correctedValue = correctedValue;
    }

    public String getReviewerComment() {
        return reviewerComment;
    }

    public void setReviewerComment(String reviewerComment) {
        this.reviewerComment = reviewerComment;
    }

    public String getAiModel() {
        return aiModel;
    }

    public void setAiModel(String aiModel) {
        this.aiModel = aiModel;
    }

    public String getAiPrompt() {
        return aiPrompt;
    }

    public void setAiPrompt(String aiPrompt) {
        this.aiPrompt = aiPrompt;
    }

    public String getAiExplanation() {
        return aiExplanation;
    }

    public void setAiExplanation(String aiExplanation) {
        this.aiExplanation = aiExplanation;
    }

    public String getAiSuggestedCorrection() {
        return aiSuggestedCorrection;
    }

    public void setAiSuggestedCorrection(String aiSuggestedCorrection) {
        this.aiSuggestedCorrection = aiSuggestedCorrection;
    }

    public String getAiReviewerNote() {
        return aiReviewerNote;
    }

    public void setAiReviewerNote(String aiReviewerNote) {
        this.aiReviewerNote = aiReviewerNote;
    }

    public String getAiSeverityRationale() {
        return aiSeverityRationale;
    }

    public void setAiSeverityRationale(String aiSeverityRationale) {
        this.aiSeverityRationale = aiSeverityRationale;
    }

    public String getAiClassifiedSeverity() {
        return aiClassifiedSeverity;
    }

    public void setAiClassifiedSeverity(String aiClassifiedSeverity) {
        this.aiClassifiedSeverity = aiClassifiedSeverity;
    }

    public String getAiConflictComparison() {
        return aiConflictComparison;
    }

    public void setAiConflictComparison(String aiConflictComparison) {
        this.aiConflictComparison = aiConflictComparison;
    }

    public LocalDateTime getAiGeneratedAt() {
        return aiGeneratedAt;
    }

    public void setAiGeneratedAt(LocalDateTime aiGeneratedAt) {
        this.aiGeneratedAt = aiGeneratedAt;
    }

    public String getAiDecision() {
        return aiDecision;
    }

    public void setAiDecision(String aiDecision) {
        this.aiDecision = aiDecision;
    }
}
