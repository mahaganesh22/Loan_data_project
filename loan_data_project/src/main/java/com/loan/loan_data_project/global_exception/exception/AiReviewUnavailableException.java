package com.loan.loan_data_project.global_exception.exception;

import org.springframework.http.HttpStatus;

public class AiReviewUnavailableException extends BaseApiException {

    public AiReviewUnavailableException() {
        super("AI review service is unavailable. Start the Python LangChain service on port 8001.", HttpStatus.SERVICE_UNAVAILABLE);
    }

    public AiReviewUnavailableException(Throwable cause) {
        super("AI review service is unavailable. Start the Python LangChain service on port 8001.", HttpStatus.SERVICE_UNAVAILABLE, cause);
    }
}
