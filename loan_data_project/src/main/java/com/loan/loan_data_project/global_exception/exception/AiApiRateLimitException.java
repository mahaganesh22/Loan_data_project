package com.loan.loan_data_project.global_exception.exception;

import org.springframework.http.HttpStatus;

public class AiApiRateLimitException extends BaseApiException {

    public AiApiRateLimitException() {
        this(null);
    }

    public AiApiRateLimitException(Throwable cause) {
        super(
                "The AI API rate limit was reached. Wait a moment and try again.",
                HttpStatus.TOO_MANY_REQUESTS,
                "AI_API_RATE_LIMIT",
                cause
        );
    }
}
