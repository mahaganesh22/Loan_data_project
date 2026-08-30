package com.loan.loan_data_project.upload_file.ingestion;

import java.io.IOException;
import java.io.Reader;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;
import org.springframework.stereotype.Component;

import com.loan.loan_data_project.loans.entity.Loan;

@Component
public class CsvParser {
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    public Loan parseLoan(CSVRecord record) {

        Loan loan = new Loan();

        loan.setLoanId(record.get("loan_id"));
        loan.setBorrowerId(record.get("borrower_id"));
        loan.setLoanType(record.get("loan_type"));

        loan.setOriginationDate(
            LocalDate.parse(record.get("origination_date"), DATE_FORMATTER)
        );

        loan.setMaturityDate(
                LocalDate.parse(record.get("maturity_date"), DATE_FORMATTER)
        );

        loan.setOriginalPrincipal(
                new BigDecimal(record.get("original_principal"))
        );

        loan.setCurrentBalance(
                new BigDecimal(record.get("current_balance"))
        );

        loan.setInterestRate(
                new BigDecimal(record.get("interest_rate"))
        );

        loan.setTermMonths(
                Integer.parseInt(record.get("term_months"))
        );

        loan.setBorrowerState(record.get("borrower_state"));
        loan.setLoanPurpose(record.get("loan_purpose"));
        loan.setCreditGrade(record.get("credit_grade"));

        loan.setEmploymentLength(
                Integer.parseInt(record.get("employment_length"))
        );

        loan.setIncomeBand(record.get("income_band"));
        loan.setPaymentStatus(record.get("payment_status"));

        loan.setDaysPastDue(
                Integer.parseInt(record.get("days_past_due"))
        );

        loan.setServicerName(record.get("servicer_name"));

        loan.setLastPaymentDate(
                LocalDate.parse(record.get("last_payment_date"), DATE_FORMATTER)
        );

        loan.setLastUpdatedAt(LocalDateTime.now());

        loan.setDocumentStatus(record.get("document_status"));
        loan.setSourceSystem(record.get("source_system"));

        return loan;
    }

    public CSVParser createParser(Reader reader) throws IOException {
        return CSVFormat.DEFAULT
                    .builder()
                    .setHeader()
                    .setSkipHeaderRecord(true)
                    .get()
                    .parse(reader);
    }
    
}