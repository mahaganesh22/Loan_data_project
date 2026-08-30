package com.loan.loan_data_project.exception.dto;

public class ExceptionReviewRequest {

    private String correctedValue;

    private String reviewerComment;

    private String aiDecision;

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

    public String getAiDecision() {
        return aiDecision;
    }

    public void setAiDecision(String aiDecision) {
        this.aiDecision = aiDecision;
    }
}
