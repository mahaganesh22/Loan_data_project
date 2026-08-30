package com.loan.loan_data_project.global_exception.exception;

import org.springframework.http.HttpStatus;

public class AiApiKeyExpiredException extends BaseApiException {

    public AiApiKeyExpiredException() {
        this(null);
    }

    public AiApiKeyExpiredException(Throwable cause) {
        super(
                "The AI API key has expired. Update OPENAI_API_KEY in ai_review/.env and restart the AI service.",
                HttpStatus.BAD_GATEWAY,
                "AI_API_EXPIRED",
                cause
        );
    }
}
