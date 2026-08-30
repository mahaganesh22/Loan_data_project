package com.loan.loan_data_project.ai.dto;

import java.util.List;

public class AiSummaryRequest {

    private List<AiSummaryItem> exceptions;

    public AiSummaryRequest() {
    }

    public AiSummaryRequest(List<AiSummaryItem> exceptions) {
        this.exceptions = exceptions;
    }

    public List<AiSummaryItem> getExceptions() {
        return exceptions;
    }

    public void setExceptions(List<AiSummaryItem> exceptions) {
        this.exceptions = exceptions;
    }
}
