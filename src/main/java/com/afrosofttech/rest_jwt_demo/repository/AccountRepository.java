package com.afrosofttech.rest_jwt_demo.repository;

import com.afrosofttech.rest_jwt_demo.entity.Account;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface AccountRepository extends JpaRepository<Account, Long> {
    Optional<Account> findByEmail(String email);
}
