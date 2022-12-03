package com.project.test.account.service;

import com.project.test.account.dto.AccountDto;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AccountService {
    List<AccountDto> getAccountsByClientId(Long clientId);
}
