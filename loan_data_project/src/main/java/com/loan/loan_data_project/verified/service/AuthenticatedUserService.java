package com.loan.loan_data_project.verified.service;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import com.loan.loan_data_project.global_exception.exception.UnauthenticatedUserException;
import com.loan.loan_data_project.security.model.CustomUserDetails;
import com.loan.loan_data_project.user.entity.User;

@Service
public class AuthenticatedUserService {

    public User getCurrentUser() {

        Authentication authentication = SecurityContextHolder
                                                .getContext()
                                                .getAuthentication();

        if (authentication == null ||
                !(authentication.getPrincipal()
                        instanceof CustomUserDetails)) {

            throw new UnauthenticatedUserException();
        }

        CustomUserDetails userDetails =
                (CustomUserDetails)
                        authentication.getPrincipal();

        return userDetails.getUser();
    }
}
