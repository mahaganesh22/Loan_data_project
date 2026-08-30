package com.loan.loan_data_project.ai.dto;

public class AiRuleResponse {

    private String model;
    private String generatedAt;
    private String prompt;
    private String suggestedRule;
    private String testIdea;

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

    public String getSuggestedRule() {
        return suggestedRule;
    }

    public void setSuggestedRule(String suggestedRule) {
        this.suggestedRule = suggestedRule;
    }

    public String getTestIdea() {
        return testIdea;
    }

    public void setTestIdea(String testIdea) {
        this.testIdea = testIdea;
    }
}
