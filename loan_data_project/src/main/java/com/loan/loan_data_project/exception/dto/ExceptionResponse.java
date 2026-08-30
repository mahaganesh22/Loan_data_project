package com.loan.loan_data_project.exception.dto;

import java.time.LocalDateTime;

import com.loan.loan_data_project.ai.dto.AiReviewResponse;
import com.loan.loan_data_project.exception.entity.LoanException;
import com.loan.loan_data_project.exception.enums.ExceptionSeverity;
import com.loan.loan_data_project.exception.enums.ExceptionStatus;
import com.loan.loan_data_project.exception.enums.ExceptionType;

public class ExceptionResponse {
    
    private Long id;
    private Long sourceLoanId;
    private String loanId;
    private String borrowerId;
    private ExceptionType exceptionType;
    private ExceptionSeverity severity;
    private String fieldName;
    private String message;
    private ExceptionStatus status;
    private LocalDateTime createdAt;
    private String originalValue;
    private String correctedValue;
    private String reviewerComment;
    private LocalDateTime reviewedAt;
    private String aiDecision;
    private AiReviewResponse aiReview;

    public ExceptionResponse(LoanException exception) {

        this.id = exception.getId();

        this.sourceLoanId = exception.getLoan().getId();

        this.loanId = exception.getLoan().getLoanId();

        this.borrowerId = exception.getLoan().getBorrowerId();

        this.exceptionType = exception.getExceptionType();

        this.severity = exception.getSeverity();

        this.fieldName = exception.getFieldName();

        this.message = exception.getMessage();

        this.status = exception.getStatus();

        this.createdAt = exception.getCreatedAt();
        this.originalValue = exception.getOriginalValue();
        this.correctedValue = exception.getCorrectedValue();
        this.reviewerComment = exception.getReviewerComment();
        this.reviewedAt = exception.getReviewedAt();
        this.aiDecision = exception.getAiDecision();
        if (exception.getAiExplanation() != null) {
            AiReviewResponse aiReview = new AiReviewResponse();
            aiReview.setModel(exception.getAiModel());
            aiReview.setGeneratedAt(exception.getAiGeneratedAt() == null ? null : exception.getAiGeneratedAt().toString());
            aiReview.setPrompt(exception.getAiPrompt());
            aiReview.setExplanation(exception.getAiExplanation());
            aiReview.setMessageContext(exception.getMessage());
            aiReview.setSuggestedCorrection(exception.getAiSuggestedCorrection());
            aiReview.setReviewerNote(exception.getAiReviewerNote());
            aiReview.setSeverityRationale(exception.getAiSeverityRationale());
            aiReview.setClassifiedSeverity(exception.getAiClassifiedSeverity());
            aiReview.setConflictComparison(exception.getAiConflictComparison());
            this.aiReview = aiReview;
        }
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

    public ExceptionType getExceptionType() {
        return exceptionType;
    }

    public ExceptionSeverity getSeverity() {
        return severity;
    }

    public String getFieldName() {
        return fieldName;
    }

    public String getMessage() {
        return message;
    }

    public ExceptionStatus getStatus() {
        return status;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public String getOriginalValue() {
        return originalValue;
    }

    public String getCorrectedValue() {
        return correctedValue;
    }

    public String getReviewerComment() {
        return reviewerComment;
    }

    public LocalDateTime getReviewedAt() {
        return reviewedAt;
    }

    public String getAiDecision() {
        return aiDecision;
    }

    public AiReviewResponse getAiReview() {
        return aiReview;
    }
}
