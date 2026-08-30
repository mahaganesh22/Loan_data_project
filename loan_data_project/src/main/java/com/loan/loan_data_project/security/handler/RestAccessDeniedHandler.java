package com.loan.loan_data_project.security.handler;

import java.io.IOException;

import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.stereotype.Component;

import com.loan.loan_data_project.global_exception.dto.ErrorResponseWriter;
import com.loan.loan_data_project.global_exception.dto.ErrorResponses;
import com.loan.loan_data_project.global_exception.exception.AccessForbiddenException;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Component
public class RestAccessDeniedHandler implements AccessDeniedHandler {

    @Override
    public void handle(
            HttpServletRequest request,
            HttpServletResponse response,
            AccessDeniedException accessDeniedException
    ) throws IOException {
        AccessForbiddenException ex = new AccessForbiddenException();
        ErrorResponseWriter.write(
                response,
                ErrorResponses.of(ex.getStatus(), ex.getMessage(), request.getRequestURI(), ex.getCode())
        );
    }
}
