package com.loan.loan_data_project.ai.dto;

public class AiSummaryResponse {

    private String model;
    private String generatedAt;
    private String prompt;
    private String summary;
    private String classifiedSeverity;
    private int openCount;

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

    public String getSummary() {
        return summary;
    }

    public void setSummary(String summary) {
        this.summary = summary;
    }

    public String getClassifiedSeverity() {
        return classifiedSeverity;
    }

    public void setClassifiedSeverity(String classifiedSeverity) {
        this.classifiedSeverity = classifiedSeverity;
    }

    public int getOpenCount() {
        return openCount;
    }

    public void setOpenCount(int openCount) {
        this.openCount = openCount;
    }
}
