package com.loan.loan_data_project.ai.client;

import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestClientResponseException;

import com.loan.loan_data_project.global_exception.exception.AiApiKeyExpiredException;
import com.loan.loan_data_project.global_exception.exception.AiApiKeyInvalidException;
import com.loan.loan_data_project.global_exception.exception.AiApiKeyMissingException;
import com.loan.loan_data_project.global_exception.exception.AiApiQuotaExceededException;
import com.loan.loan_data_project.global_exception.exception.AiApiRateLimitException;
import com.loan.loan_data_project.global_exception.exception.AiReviewUnavailableException;
import com.loan.loan_data_project.global_exception.exception.BaseApiException;

final class AiReviewErrorMapper {

    private AiReviewErrorMapper() {
    }

    static BaseApiException map(RestClientResponseException ex) {
        String body = ex.getResponseBodyAsString();
        String haystack = ((body == null ? "" : body) + " " + ex.getMessage()).toLowerCase();
        int status = ex.getStatusCode().value();

        if (containsAny(haystack, "openai_api_key is not set", "api key is not configured", "missing_api_key")
                || status == 503 && containsAny(haystack, "openai_api_key", "api key")) {
            return new AiApiKeyMissingException(ex);
        }
        if (containsAny(haystack, "insufficient_quota", "exceeded your current quota", "billing_not_active", "quota")
                || status == 402) {
            return new AiApiQuotaExceededException(ex);
        }
        if (containsAny(haystack, "rate_limit", "too many requests") || status == 429) {
            return new AiApiRateLimitException(ex);
        }
        if (containsAny(haystack, "expired", "key has expired", "token has expired")
                && containsAny(haystack, "key", "token", "api")) {
            return new AiApiKeyExpiredException(ex);
        }
        if (containsAny(haystack, "invalid_api_key", "incorrect api key", "invalid api key", "authenticationerror")
                || status == 401 || status == 403) {
            return new AiApiKeyInvalidException(ex);
        }
        if (status >= 500 || status == 404) {
            return new AiReviewUnavailableException(ex);
        }
        return new AiReviewUnavailableException(ex);
    }

    static BaseApiException map(ResourceAccessException ex) {
        return new AiReviewUnavailableException(ex);
    }

    private static boolean containsAny(String haystack, String... needles) {
        for (String needle : needles) {
            if (haystack.contains(needle)) {
                return true;
            }
        }
        return false;
    }
}
