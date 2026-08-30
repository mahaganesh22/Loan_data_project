package com.loan.loan_data_project.ai.dto;

public class AiReviewResponse {

    private String model;
    private String generatedAt;
    private String prompt;
    private String explanation;
    private String messageContext;
    private String suggestedCorrection;
    private String reviewerNote;
    private String severityRationale;
    private String classifiedSeverity;
    private String conflictComparison;

    public String getModel() {
        return model;
    }

    public void setModel(String model) {
        this.model = model;
    }

    public String getGeneratedAt() {
        return generatedAt;
    }

    public void setGeneratedAt(String generatedAt) {
        this.generatedAt = generatedAt;
    }

    public String getPrompt() {
        return prompt;
    }

    public void setPrompt(String prompt) {
        this.prompt = prompt;
    }

    public String getExplanation() {
        return explanation;
    }

    public void setExplanation(String explanation) {
        this.explanation = explanation;
    }

    public String getMessageContext() {
        return messageContext;
    }

    public void setMessageContext(String messageContext) {
        this.messageContext = messageContext;
    }

    public String getSuggestedCorrection() {
        return suggestedCorrection;
    }

    public void setSuggestedCorrection(String suggestedCorrection) {
        this.suggestedCorrection = suggestedCorrection;
    }

    public String getReviewerNote() {
        return reviewerNote;
    }

    public void setReviewerNote(String reviewerNote) {
        this.reviewerNote = reviewerNote;
    }

    public String getSeverityRationale() {
        return severityRationale;
    }

    public void setSeverityRationale(String severityRationale) {
        this.severityRationale = severityRationale;
    }

    public String getClassifiedSeverity() {
        return classifiedSeverity;
    }

    public void setClassifiedSeverity(String classifiedSeverity) {
        this.classifiedSeverity = classifiedSeverity;
    }

    public String getConflictComparison() {
        return conflictComparison;
    }

    public void setConflictComparison(String conflictComparison) {
        this.conflictComparison = conflictComparison;
    }
}
