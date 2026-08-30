package com.loan.loan_data_project.user.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.loan.loan_data_project.user.entity.User;

public interface UserRepository extends JpaRepository<User, Long>{
    Optional<User> findByUsername(String name);
    boolean existsByUsername(String name);
}
