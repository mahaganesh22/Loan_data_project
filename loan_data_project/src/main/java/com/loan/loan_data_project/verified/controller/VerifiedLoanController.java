package com.loan.loan_data_project.verified.controller;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.loan.loan_data_project.global_exception.exception.VerifiedLoanNotFoundException;
import com.loan.loan_data_project.verified.dto.VerifiedLoanResponse;
import com.loan.loan_data_project.verified.entity.VerifiedLoan;
import com.loan.loan_data_project.verified.repository.VerifiedLoanRepository;
import com.loan.loan_data_project.verified.service.VerifiedLoanExportService;
import com.loan.loan_data_project.verified.service.VerifiedLoanService;


@RestController
@RequestMapping("/api/verified-loans")
public class VerifiedLoanController {

    private final VerifiedLoanService verifiedLoanService;
    private final VerifiedLoanRepository verifiedLoanRepository;
    private final VerifiedLoanExportService verifiedLoanExportService;

    public VerifiedLoanController(
            VerifiedLoanService verifiedLoanService,
            VerifiedLoanRepository verifiedLoanRepository,
            VerifiedLoanExportService verifiedLoanExportService
    ) {
        this.verifiedLoanService = verifiedLoanService;
        this.verifiedLoanRepository = verifiedLoanRepository;
        this.verifiedLoanExportService = verifiedLoanExportService;
    }

    @PostMapping("/{loanId}/verify")
    public ResponseEntity<VerifiedLoanResponse> verifyLoan(
            @PathVariable Long loanId
    ) {

        VerifiedLoan verifiedLoan =
                verifiedLoanService.verifyLoan(
                        loanId
                );

        return ResponseEntity.ok(
                new VerifiedLoanResponse(verifiedLoan)
        );
    }

    @GetMapping("/export")
    public ResponseEntity<byte[]> exportVerifiedLoans() {

        String csv =
                verifiedLoanExportService.generateCsv();

        byte[] bytes =
                csv.getBytes(
                        java.nio.charset.StandardCharsets.UTF_8
                );

        return ResponseEntity.ok()
                .header(
                        "Content-Disposition",
                        "attachment; filename=verified_loans.csv"
                )
                .header(
                        "Content-Type",
                        "text/csv"
                )
                .body(bytes);
        }

    @GetMapping("/{id}")
    public ResponseEntity<VerifiedLoanResponse> getVerifiedLoan(@PathVariable Long id) {

        VerifiedLoan verifiedLoan =
                verifiedLoanRepository.findById(id)
                        .orElseThrow(() ->
                                new VerifiedLoanNotFoundException(id)
                        );

        return ResponseEntity.ok(
                new VerifiedLoanResponse(verifiedLoan)
        );
    }

    @GetMapping
    public ResponseEntity<Page<VerifiedLoanResponse>> getVerifiedLoans(
                @RequestParam(required = false) String loanId,
                @RequestParam(required = false) String borrowerId,
                Pageable pageable
        ) {

        Page<VerifiedLoan> verifiedLoans;

        if (loanId != null && borrowerId != null) {

                verifiedLoans =
                        verifiedLoanService
                                .getByLoanIdAndBorrowerId(
                                        loanId,
                                        borrowerId,
                                        pageable
                                );
        } else if (loanId != null) {

                verifiedLoans =
                        verifiedLoanService
                                .getByLoanId(
                                        loanId,
                                        pageable
                                );
        } else if (borrowerId != null) {

                verifiedLoans =
                        verifiedLoanService
                                .getByBorrowerId(
                                        borrowerId,
                                        pageable
                                );
        } else {

                verifiedLoans =
                        verifiedLoanService
                                .getVerifiedLoans(
                                        pageable
                                );
        }

        Page<VerifiedLoanResponse> response =
                verifiedLoans.map(
                        VerifiedLoanResponse::new
                );

        return ResponseEntity.ok(response);
    }
}
