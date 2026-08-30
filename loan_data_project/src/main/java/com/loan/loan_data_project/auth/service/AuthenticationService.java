package com.loan.loan_data_project.auth.service;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Service;

import com.loan.loan_data_project.auth.dto.AuthenticationResponse;
import com.loan.loan_data_project.auth.dto.LoginRequest;
import com.loan.loan_data_project.global_exception.exception.AccountDisabledException;
import com.loan.loan_data_project.global_exception.exception.InvalidCredentialsException;
import com.loan.loan_data_project.security.jwt.JwtService;
import com.loan.loan_data_project.security.model.CustomUserDetails;

@Service
public class AuthenticationService {

    private final AuthenticationManager authenticationManager;
    private final UserDetailsService userDetailsService;
    private final JwtService jwtService;

    public AuthenticationService(
                                    AuthenticationManager authenticationManager,
                                    UserDetailsService userDetailsService,
                                    JwtService jwtService
                                ) {
        this.authenticationManager = authenticationManager;
        this.userDetailsService = userDetailsService;
        this.jwtService = jwtService;
    }

    public AuthenticationResponse login(LoginRequest request) {
        try {
            authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                    request.getUsername(),
                    request.getPassword()
                )
            );
        } catch (DisabledException ex) {
            throw new AccountDisabledException();
        } catch (AuthenticationException ex) {
            throw new InvalidCredentialsException();
        }

        UserDetails userDetails = userDetailsService.loadUserByUsername(request.getUsername());
        String jwtToken = jwtService.generateToken(userDetails);

        String role = "";
        if (userDetails instanceof CustomUserDetails customUserDetails) {
            role = customUserDetails.getUser().getRole().name();
        }

        return new AuthenticationResponse(
                jwtToken,
                userDetails.getUsername(),
                role
        );
    }
}
