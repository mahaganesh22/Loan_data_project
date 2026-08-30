package com.loan.loan_data_project.global_exception.exception;

import org.springframework.http.HttpStatus;

public class AiApiKeyMissingException extends BaseApiException {

    public AiApiKeyMissingException() {
        this(null);
    }

    public AiApiKeyMissingException(Throwable cause) {
        super(
                "The AI API key is not configured. Copy ai_review/.env.example to ai_review/.env, add OPENAI_API_KEY, and restart the AI service.",
                HttpStatus.SERVICE_UNAVAILABLE,
                "AI_API_KEY_MISSING",
                cause
        );
    }
}
