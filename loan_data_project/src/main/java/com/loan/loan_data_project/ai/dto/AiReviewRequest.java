package com.loan.loan_data_project.ai.dto;

import java.util.Map;

public class AiReviewRequest {

    private Map<String, Object> exception;
    private Map<String, Object> loan;

    public AiReviewRequest() {
    }

    public AiReviewRequest(Map<String, Object> exception, Map<String, Object> loan) {
        this.exception = exception;
        this.loan = loan;
    }

    public Map<String, Object> getException() {
        return exception;
    }

    public void setException(Map<String, Object> exception) {
        this.exception = exception;
    }

    public Map<String, Object> getLoan() {
        return loan;
    }

    public void setLoan(Map<String, Object> loan) {
        this.loan = loan;
    }
}
