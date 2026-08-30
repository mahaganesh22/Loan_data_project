package com.loan.loan_data_project.global_exception.exception;

import org.springframework.http.HttpStatus;

public class AiApiKeyInvalidException extends BaseApiException {

    public AiApiKeyInvalidException() {
        this(null);
    }

    public AiApiKeyInvalidException(Throwable cause) {
        super(
                "The AI API key is invalid. Check OPENAI_API_KEY in ai_review/.env, then restart the AI service.",
                HttpStatus.BAD_GATEWAY,
                "AI_API_INVALID_KEY",
                cause
        );
    }
}
