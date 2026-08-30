package com.loan.loan_data_project.ai.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.loan.loan_data_project.ai.dto.AiDecisionRequest;
import com.loan.loan_data_project.ai.dto.AiReviewResponse;
import com.loan.loan_data_project.ai.dto.AiRuleRequest;
import com.loan.loan_data_project.ai.dto.AiRuleResponse;
import com.loan.loan_data_project.ai.dto.AiSummaryResponse;
import com.loan.loan_data_project.ai.service.AiReviewService;
import com.loan.loan_data_project.exception.dto.ExceptionResponse;
import com.loan.loan_data_project.exception.enums.ExceptionSeverity;
import com.loan.loan_data_project.exception.enums.ExceptionStatus;

@RestController
@RequestMapping("/api")
public class AiReviewController {

    private final AiReviewService aiReviewService;

    public AiReviewController(AiReviewService aiReviewService) {
        this.aiReviewService = aiReviewService;
    }

    @PostMapping("/exceptions/{id}/ai-review")
    public ResponseEntity<AiReviewResponse> reviewException(@PathVariable Long id) {
        return ResponseEntity.ok(aiReviewService.reviewException(id));
    }

    @PatchMapping("/exceptions/{id}/ai-decision")
    public ResponseEntity<ExceptionResponse> recordDecision(
            @PathVariable Long id,
            @RequestBody AiDecisionRequest request
    ) {
        return ResponseEntity.ok(new ExceptionResponse(aiReviewService.recordDecision(id, request)));
    }

    @GetMapping("/ai/summary")
    public ResponseEntity<AiSummaryResponse> summarizeQueue(
            @RequestParam(required = false) ExceptionStatus status,
            @RequestParam(required = false) ExceptionSeverity severity
    ) {
        return ResponseEntity.ok(aiReviewService.summarizeQueue(status, severity));
    }

    @PostMapping("/ai/rules")
    public ResponseEntity<AiRuleResponse> generateRule(@RequestBody AiRuleRequest request) {
        return ResponseEntity.ok(aiReviewService.generateRule(request));
    }
}
