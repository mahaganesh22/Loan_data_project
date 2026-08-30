package com.loan.loan_data_project.ai.service;

import java.time.LocalDateTime;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.loan.loan_data_project.ai.client.AiReviewClient;
import com.loan.loan_data_project.ai.dto.AiDecisionRequest;
import com.loan.loan_data_project.ai.dto.AiReviewRequest;
import com.loan.loan_data_project.ai.dto.AiReviewResponse;
import com.loan.loan_data_project.ai.dto.AiRuleRequest;
import com.loan.loan_data_project.ai.dto.AiRuleResponse;
import com.loan.loan_data_project.ai.dto.AiSummaryItem;
import com.loan.loan_data_project.ai.dto.AiSummaryRequest;
import com.loan.loan_data_project.ai.dto.AiSummaryResponse;
import com.loan.loan_data_project.audits.enums.AuditAction;
import com.loan.loan_data_project.audits.service.AuditService;
import com.loan.loan_data_project.exception.entity.LoanException;
import com.loan.loan_data_project.exception.enums.ExceptionSeverity;
import com.loan.loan_data_project.exception.enums.ExceptionStatus;
import com.loan.loan_data_project.exception.repository.LoanExceptionRepository;
import com.loan.loan_data_project.exception.service.ExceptionService;
import com.loan.loan_data_project.global_exception.exception.UnauthorizedReviewException;
import com.loan.loan_data_project.loans.entity.Loan;
import com.loan.loan_data_project.user.entity.User;
import com.loan.loan_data_project.user.enums.UserRole;
import com.loan.loan_data_project.verified.service.AuthenticatedUserService;

@Service
public class AiReviewService {

    private static final Set<String> ALLOWED_DECISIONS = Set.of("ACCEPTED", "REJECTED", "EDITED");

    private final AiReviewClient aiReviewClient;
    private final ExceptionService exceptionService;
    private final LoanExceptionRepository loanExceptionRepository;
    private final AuditService auditService;
    private final AuthenticatedUserService authenticatedUserService;

    public AiReviewService(
            AiReviewClient aiReviewClient,
            ExceptionService exceptionService,
            LoanExceptionRepository loanExceptionRepository,
            AuditService auditService,
            AuthenticatedUserService authenticatedUserService
    ) {
        this.aiReviewClient = aiReviewClient;
        this.exceptionService = exceptionService;
        this.loanExceptionRepository = loanExceptionRepository;
        this.auditService = auditService;
        this.authenticatedUserService = authenticatedUserService;
    }

    @Transactional
    public AiReviewResponse reviewException(Long exceptionId) {
        User reviewer = requireReviewer();
        LoanException exception = exceptionService.getExceptionById(exceptionId);
        Loan loan = exception.getLoan();

        AiReviewResponse response = aiReviewClient.review(
                new AiReviewRequest(toExceptionMap(exception), toLoanMap(loan))
        );

        exception.setAiModel(response.getModel());
        exception.setAiPrompt(trim(response.getPrompt(), 2000));
        exception.setAiExplanation(trim(response.getExplanation(), 2000));
        exception.setAiSuggestedCorrection(response.getSuggestedCorrection());
        exception.setAiReviewerNote(trim(response.getReviewerNote(), 2000));
        exception.setAiSeverityRationale(trim(response.getSeverityRationale(), 1000));
        exception.setAiClassifiedSeverity(response.getClassifiedSeverity());
        exception.setAiConflictComparison(trim(response.getConflictComparison(), 2000));
        exception.setAiGeneratedAt(LocalDateTime.now());
        exception.setAiDecision(null);
        loanExceptionRepository.save(exception);

        auditService.log(
                AuditAction.AI_RECOMMENDATION_GENERATED,
                "EXCEPTION",
                exception.getId(),
                reviewer,
                "Model=" + response.getModel()
                        + "; prompt=" + response.getPrompt()
                        + "; suggestion=" + nullToDash(response.getSuggestedCorrection())
        );

        return response;
    }

    @Transactional
    public LoanException recordDecision(Long exceptionId, AiDecisionRequest request) {
        User reviewer = requireReviewer();
        String decision = request.getDecision() == null ? "" : request.getDecision().trim().toUpperCase();
        if (!ALLOWED_DECISIONS.contains(decision)) {
            throw new IllegalArgumentException("AI decision must be ACCEPTED, REJECTED, or EDITED");
        }

        LoanException exception = exceptionService.getExceptionById(exceptionId);
        exception.setAiDecision(decision);
        loanExceptionRepository.save(exception);

        auditService.log(
                AuditAction.AI_DECISION_RECORDED,
                "EXCEPTION",
                exception.getId(),
                reviewer,
                "Reviewer marked AI suggestion as " + decision + ". Loan data was not changed by this action."
        );

        return exception;
    }

    @Transactional
    public AiSummaryResponse summarizeQueue(ExceptionStatus status, ExceptionSeverity severity) {
        User reviewer = requireReviewer();
        List<LoanException> exceptions;
        if (status != null) {
            exceptions = exceptionService.getByStatus(status);
        } else if (severity != null) {
            exceptions = exceptionService.getBySeverity(severity);
        } else {
            exceptions = exceptionService.getAllExceptions();
        }

        List<AiSummaryItem> items = exceptions.stream()
                .limit(75)
                .map(this::toSummaryItem)
                .toList();

        AiSummaryResponse response = aiReviewClient.summarize(new AiSummaryRequest(items));

        auditService.log(
                AuditAction.AI_RECOMMENDATION_GENERATED,
                "EXCEPTION",
                0L,
                reviewer,
                "Batch AI summary generated for " + items.size() + " exceptions. Model=" + response.getModel()
        );
        return response;
    }

    @Transactional
    public AiRuleResponse generateRule(AiRuleRequest request) {
        if (request.getInstruction() == null || request.getInstruction().isBlank()) {
            throw new IllegalArgumentException("instruction is required");
        }
        User user = authenticatedUserService.getCurrentUser();
        if (user.getRole() != UserRole.DATA_OPERATOR && user.getRole() != UserRole.REVIEWER) {
            throw new UnauthorizedReviewException();
        }

        AiRuleResponse response = aiReviewClient.generateRule(request);
        auditService.log(
                AuditAction.AI_RECOMMENDATION_GENERATED,
                "VALIDATION_RULE",
                0L,
                user,
                "Natural-language rule generated. Model=" + response.getModel() + "; prompt=" + response.getPrompt()
        );
        return response;
    }

    private User requireReviewer() {
        User reviewer = authenticatedUserService.getCurrentUser();
        if (reviewer.getRole() != UserRole.REVIEWER) {
            throw new UnauthorizedReviewException();
        }
        return reviewer;
    }

    private AiSummaryItem toSummaryItem(LoanException exception) {
        AiSummaryItem item = new AiSummaryItem();
        item.setId(exception.getId());
        item.setLoanId(exception.getLoan().getLoanId());
        item.setExceptionType(exception.getExceptionType().name());
        item.setSeverity(exception.getSeverity().name());
        item.setFieldName(exception.getFieldName());
        item.setMessage(exception.getMessage());
        return item;
    }

    private Map<String, Object> toExceptionMap(LoanException exception) {
        Map<String, Object> map = new LinkedHashMap<>();
        map.put("id", exception.getId());
        map.put("loanId", exception.getLoan().getLoanId());
        map.put("borrowerId", exception.getLoan().getBorrowerId());
        map.put("exceptionType", exception.getExceptionType().name());
        map.put("severity", exception.getSeverity().name());
        map.put("fieldName", exception.getFieldName());
        map.put("message", exception.getMessage());
        map.put("originalValue", exception.getOriginalValue());
        map.put("status", exception.getStatus().name());
        return map;
    }

    private Map<String, Object> toLoanMap(Loan loan) {
        Map<String, Object> map = new LinkedHashMap<>();
        map.put("loanId", loan.getLoanId());
        map.put("borrowerId", loan.getBorrowerId());
        map.put("loanType", loan.getLoanType());
        map.put("originationDate", loan.getOriginationDate());
        map.put("maturityDate", loan.getMaturityDate());
        map.put("originalPrincipal", loan.getOriginalPrincipal());
        map.put("currentBalance", loan.getCurrentBalance());
        map.put("interestRate", loan.getInterestRate());
        map.put("termMonths", loan.getTermMonths());
        map.put("borrowerState", loan.getBorrowerState());
        map.put("paymentStatus", loan.getPaymentStatus());
        map.put("daysPastDue", loan.getDaysPastDue());
        map.put("documentStatus", loan.getDocumentStatus());
        map.put("sourceSystem", loan.getSourceSystem());
        map.put("lastUpdatedAt", loan.getLastUpdatedAt());
        return map;
    }

    private String trim(String value, int max) {
        if (value == null) {
            return null;
        }
        return value.length() <= max ? value : value.substring(0, max);
    }

    private String nullToDash(String value) {
        return value == null || value.isBlank() ? "-" : value;
    }
}
