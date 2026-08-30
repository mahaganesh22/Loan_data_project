package com.loan.loan_data_project.global_exception.exception;

import org.springframework.http.HttpStatus;

public class AiApiQuotaExceededException extends BaseApiException {

    public AiApiQuotaExceededException() {
        this(null);
    }

    public AiApiQuotaExceededException(Throwable cause) {
        super(
                "The AI API quota or billing limit has been reached. Check your OpenAI account billing and try again later.",
                HttpStatus.PAYMENT_REQUIRED,
                "AI_API_QUOTA_EXCEEDED",
                cause
        );
    }
}
