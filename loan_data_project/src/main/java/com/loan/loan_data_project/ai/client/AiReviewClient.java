package com.loan.loan_data_project.ai.client;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestClientResponseException;

import com.loan.loan_data_project.ai.dto.AiReviewRequest;
import com.loan.loan_data_project.ai.dto.AiReviewResponse;
import com.loan.loan_data_project.ai.dto.AiRuleRequest;
import com.loan.loan_data_project.ai.dto.AiRuleResponse;
import com.loan.loan_data_project.ai.dto.AiSummaryRequest;
import com.loan.loan_data_project.ai.dto.AiSummaryResponse;
import com.loan.loan_data_project.global_exception.exception.AiReviewUnavailableException;

@Component
public class AiReviewClient {

    private final RestClient restClient;

    public AiReviewClient(@Qualifier("aiReviewRestClient") RestClient aiReviewRestClient) {
        this.restClient = aiReviewRestClient;
    }

    public AiReviewResponse review(AiReviewRequest request) {
        return invoke(() -> restClient.post()
                .uri("/review")
                .body(request)
                .retrieve()
                .body(AiReviewResponse.class));
    }

    public AiSummaryResponse summarize(AiSummaryRequest request) {
        return invoke(() -> restClient.post()
                .uri("/summarize")
                .body(request)
                .retrieve()
                .body(AiSummaryResponse.class));
    }

    public AiRuleResponse generateRule(AiRuleRequest request) {
        return invoke(() -> restClient.post()
                .uri("/rules")
                .body(request)
                .retrieve()
                .body(AiRuleResponse.class));
    }

    // private <T> T invoke(AiCall<T> call) {
    //     try {
    //         T response = call.execute();
    //         if (response == null) {
    //             throw new AiReviewUnavailableException();
    //         }
    //         return response;
    //     } catch (RestClientResponseException ex) {
    //         throw AiReviewErrorMapper.map(ex);
    //     } catch (ResourceAccessException ex) {
    //         throw AiReviewErrorMapper.map(ex);
    //     } catch (RestClientException ex) {
    //         throw new AiReviewUnavailableException(ex);
    //     }
    // }

    private <T> T invoke(AiCall<T> call) {

        int maxAttempts = 3;

        for (int attempt = 1; attempt <= maxAttempts; attempt++) {
            try {
                T response = call.execute();

                if (response == null) {
                    throw new AiReviewUnavailableException();
                }

                return response;

            } catch (ResourceAccessException ex) {

                if (attempt == maxAttempts) {
                    throw AiReviewErrorMapper.map(ex);
                }

                try {
                    Thread.sleep(2000L * attempt);
                } catch (InterruptedException interruptedException) {
                    Thread.currentThread().interrupt();
                    throw AiReviewErrorMapper.map(ex);
                }

            } catch (RestClientResponseException ex) {
                throw AiReviewErrorMapper.map(ex);

            } catch (RestClientException ex) {
                throw new AiReviewUnavailableException(ex);
            }
        }

        throw new AiReviewUnavailableException();
    }

    @FunctionalInterface
    private interface AiCall<T> {
        T execute();
    }
}
