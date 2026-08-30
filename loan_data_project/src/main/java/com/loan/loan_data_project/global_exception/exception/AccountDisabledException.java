package com.loan.loan_data_project.global_exception.exception;

import org.springframework.http.HttpStatus;

public class AccountDisabledException extends BaseApiException {

    public AccountDisabledException() {
        super(
                "This account is disabled. Contact an administrator to restore access.",
                HttpStatus.FORBIDDEN,
                "ACCOUNT_DISABLED"
        );
    }
}
