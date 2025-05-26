package com.afrosofttech.rest_jwt_demo.config;

import com.afrosofttech.rest_jwt_demo.entity.Account;
import com.afrosofttech.rest_jwt_demo.service.AccountService;
import com.afrosofttech.rest_jwt_demo.util.constants.Authority;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
public class SeedData implements CommandLineRunner {
    @Autowired
    private AccountService accountService;

    @Override
    public void run(String... args) throws Exception {
        Account account1 = Account.builder()
                .email("user1@user.com")
                .password("password1")
                .authorities(Authority.USER.name())
                .build();
        Account account2 = Account.builder()
                .email("admin@admin.com")
                .password("password2")
                .authorities(Authority.ADMIN.name() + " " + Authority.USER.name())
                .build();
        accountService.save(account1);
        accountService.save(account2);
    }
}
