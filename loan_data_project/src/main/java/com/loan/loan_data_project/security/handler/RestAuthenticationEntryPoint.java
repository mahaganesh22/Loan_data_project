package com.loan.loan_data_project.security.handler;

import java.io.IOException;

import org.springframework.http.HttpStatus;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import com.loan.loan_data_project.global_exception.dto.ErrorResponseWriter;
import com.loan.loan_data_project.global_exception.dto.ErrorResponses;
import com.loan.loan_data_project.global_exception.exception.AuthenticationRequiredException;
import com.loan.loan_data_project.global_exception.exception.SessionExpiredException;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Component
public class RestAuthenticationEntryPoint implements AuthenticationEntryPoint {

    @Override
    public void commence(
            HttpServletRequest request,
            HttpServletResponse response,
            AuthenticationException authException
    ) throws IOException {
        String authorization = request.getHeader("Authorization");
        boolean hadBearerToken = authorization != null && authorization.startsWith("Bearer ");

        HttpStatus status;
        String message;
        String code;
        if (hadBearerToken) {
            SessionExpiredException ex = new SessionExpiredException();
            status = ex.getStatus();
            message = ex.getMessage();
            code = ex.getCode();
        } else {
            AuthenticationRequiredException ex = new AuthenticationRequiredException();
            status = ex.getStatus();
            message = ex.getMessage();
            code = ex.getCode();
        }

        ErrorResponseWriter.write(
                response,
                ErrorResponses.of(status, message, request.getRequestURI(), code)
        );
    }
}
