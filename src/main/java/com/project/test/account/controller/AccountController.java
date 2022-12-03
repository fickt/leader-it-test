package com.project.test.account.controller;

import com.project.test.account.dto.AccountDto;
import com.project.test.account.service.AccountService;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.RequiredArgsConstructor;


import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@Slf4j
@RequiredArgsConstructor
@Validated
@RequestMapping("/api/v1/accounts")
public class AccountController {
    private static final String ENDPOINT_GET_CLIENT_ACCOUNTS_BY_CLIENT_ID = "clients/{clientId}";
    private final AccountService accountService;

    @GetMapping(ENDPOINT_GET_CLIENT_ACCOUNTS_BY_CLIENT_ID)
    public List<AccountDto> getAccountsByClientId(@PositiveOrZero @PathVariable Long clientId) {
        log.info("GET /api/v1/accounts/clients/{}", clientId);
        return accountService.getAccountsByClientId(clientId);
    }
}
