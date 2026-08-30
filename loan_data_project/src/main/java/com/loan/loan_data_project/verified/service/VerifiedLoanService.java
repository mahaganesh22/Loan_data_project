package com.loan.loan_data_project.verified.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.loan.loan_data_project.global_exception.exception.LoanAlreadyVerifiedException;
import com.loan.loan_data_project.global_exception.exception.LoanNotFoundException;
import com.loan.loan_data_project.global_exception.exception.RecordHashGenerationException;
import com.loan.loan_data_project.global_exception.exception.ReviewerOnlyException;
import com.loan.loan_data_project.global_exception.exception.UnresolvedLoanExceptionsException;
import com.loan.loan_data_project.audits.enums.AuditAction;
import com.loan.loan_data_project.audits.service.AuditService;
import com.loan.loan_data_project.exception.entity.LoanException;
import com.loan.loan_data_project.exception.enums.ExceptionStatus;
import com.loan.loan_data_project.exception.repository.LoanExceptionRepository;
import com.loan.loan_data_project.loans.entity.Loan;
import com.loan.loan_data_project.loans.repository.LoanRepository;
import com.loan.loan_data_project.user.entity.User;
import com.loan.loan_data_project.user.enums.UserRole;
import com.loan.loan_data_project.verified.entity.VerifiedLoan;
import com.loan.loan_data_project.verified.enums.VerificationStatus;
import com.loan.loan_data_project.verified.repository.VerifiedLoanRepository;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class VerifiedLoanService {

    private final VerifiedLoanRepository verifiedLoanRepository;
    private final LoanExceptionRepository loanExceptionRepository;
    private final LoanRepository loanRepository;
    private final AuthenticatedUserService authenticatedUserService;
    private final AuditService auditService;

    public VerifiedLoanService(
            VerifiedLoanRepository verifiedLoanRepository,
            LoanExceptionRepository loanExceptionRepository,
            LoanRepository loanRepository,
            AuthenticatedUserService authenticatedUserService,
            AuditService auditService
    ) {
        this.verifiedLoanRepository = verifiedLoanRepository;
        this.loanExceptionRepository = loanExceptionRepository;
        this.loanRepository = loanRepository;
        this.authenticatedUserService = authenticatedUserService;
        this.auditService = auditService;
    }

    @Transactional
    public VerifiedLoan verifyLoan(Long loanId) {

        Loan loan =
                loanRepository.findById(loanId)
                        .orElseThrow(() ->
                                new LoanNotFoundException(loanId)
                        );

        User reviewer = authenticatedUserService.getCurrentUser();

        if (reviewer.getRole() != UserRole.REVIEWER) {

            throw new ReviewerOnlyException();
        }

        List<LoanException> exceptionsForLoan =
                loanExceptionRepository.findByLoan_Id(loanId);

        List<LoanException> openExceptions =
                exceptionsForLoan.stream()
                        .filter(exception ->
                                exception.getStatus()
                                        == ExceptionStatus.OPEN)
                        .toList();

        if (!openExceptions.isEmpty()) {

            throw new UnresolvedLoanExceptionsException();
        }

        if (verifiedLoanRepository
                .existsBySourceLoanId(loanId)) {

            throw new LoanAlreadyVerifiedException();
        }

        VerifiedLoan verifiedLoan =
                createSnapshot(loan);

        verifiedLoan.setVerifiedBy(reviewer);

        verifiedLoan.setStatus(
                VerificationStatus.VERIFIED
        );

        verifiedLoan.setVerifiedAt(
                LocalDateTime.now()
        );

        verifiedLoan.setRecordHash(
                generateRecordHash(verifiedLoan)
        );

        String aiRecommendation = exceptionsForLoan.stream()
                .map(ex -> {
                    String decision = ex.getAiDecision() == null ? "NONE" : ex.getAiDecision();
                    String note = ex.getAiReviewerNote() == null ? "-" : ex.getAiReviewerNote();
                    return "exception " + ex.getId() + " decision=" + decision + " note=" + note;
                })
                .reduce((left, right) -> left + " | " + right)
                .map(text -> text.length() <= 2000 ? text : text.substring(0, 2000))
                .orElse(null);
        verifiedLoan.setAiRecommendation(aiRecommendation);

        VerifiedLoan saved =
                verifiedLoanRepository.save(
                        verifiedLoan
                );

        auditService.log(
                AuditAction.LOAN_VERIFIED,
                "LOAN",
                loan.getId(),
                reviewer,
                "Loan verified successfully"
        );

        return saved;
    }


    private VerifiedLoan createSnapshot(
            Loan loan
    ) {

        VerifiedLoan verifiedLoan =
                new VerifiedLoan();

        verifiedLoan.setSourceLoanId(
                loan.getId()
        );

        verifiedLoan.setLoanId(
                loan.getLoanId()
        );

        verifiedLoan.setBorrowerId(
                loan.getBorrowerId()
        );

        verifiedLoan.setLoanType(
                loan.getLoanType()
        );

        verifiedLoan.setOriginationDate(
                loan.getOriginationDate()
        );

        verifiedLoan.setMaturityDate(
                loan.getMaturityDate()
        );

        verifiedLoan.setOriginalPrincipal(
                loan.getOriginalPrincipal()
        );

        verifiedLoan.setCurrentBalance(
                loan.getCurrentBalance()
        );

        verifiedLoan.setInterestRate(
                loan.getInterestRate()
        );

        verifiedLoan.setTermMonths(
                loan.getTermMonths()
        );

        verifiedLoan.setBorrowerState(
                loan.getBorrowerState()
        );

        verifiedLoan.setLoanPurpose(
                loan.getLoanPurpose()
        );

        verifiedLoan.setCreditGrade(
                loan.getCreditGrade()
        );

        verifiedLoan.setEmploymentLength(
                loan.getEmploymentLength()
        );

        verifiedLoan.setIncomeBand(
                loan.getIncomeBand()
        );

        verifiedLoan.setPaymentStatus(
                loan.getPaymentStatus()
        );

        verifiedLoan.setDaysPastDue(
                loan.getDaysPastDue()
        );

        verifiedLoan.setServicerName(
                loan.getServicerName()
        );

        verifiedLoan.setLastPaymentDate(
                loan.getLastPaymentDate()
        );

        verifiedLoan.setDocumentStatus(
                loan.getDocumentStatus()
        );

        verifiedLoan.setSourceSystem(
                loan.getSourceSystem()
        );

        return verifiedLoan;
    }


    private String generateRecordHash(
            VerifiedLoan loan
    ) {

        try {

            String data =
                    String.valueOf(loan.getLoanId())
                            + "|"
                            + String.valueOf(
                                    loan.getBorrowerId())
                            + "|"
                            + String.valueOf(
                                    loan.getLoanType())
                            + "|"
                            + String.valueOf(
                                    loan.getOriginationDate())
                            + "|"
                            + String.valueOf(
                                    loan.getMaturityDate())
                            + "|"
                            + String.valueOf(
                                    loan.getOriginalPrincipal())
                            + "|"
                            + String.valueOf(
                                    loan.getCurrentBalance())
                            + "|"
                            + String.valueOf(
                                    loan.getInterestRate())
                            + "|"
                            + String.valueOf(
                                    loan.getTermMonths());

            MessageDigest digest =
                    MessageDigest.getInstance("SHA-256");

            byte[] hash =
                    digest.digest(
                            data.getBytes(
                                    StandardCharsets.UTF_8
                            )
                    );

            StringBuilder result =
                    new StringBuilder();

            for (byte b : hash) {

                result.append(
                        String.format(
                                "%02x",
                                b
                        )
                );
            }

            return result.toString();

        } catch (Exception e) {

            throw new RecordHashGenerationException(e);
        }
    }

    public Page<VerifiedLoan> getVerifiedLoans(Pageable pageable) {

        return verifiedLoanRepository.findAll(
                pageable
        );
    }

    public Page<VerifiedLoan> getByLoanId(String loanId, Pageable pageable) {

        return verifiedLoanRepository.findByLoanId(
                loanId,
                pageable
        );
    }

    public Page<VerifiedLoan> getByBorrowerId(String borrowerId, Pageable pageable) {

        return verifiedLoanRepository.findByBorrowerId(
                borrowerId,
                pageable
        );
    }

    public Page<VerifiedLoan> getByLoanIdAndBorrowerId(String loanId, String borrowerId, Pageable pageable) {

        return verifiedLoanRepository
                .findByLoanIdAndBorrowerId(
                        loanId,
                        borrowerId,
                        pageable
                );
    }
}