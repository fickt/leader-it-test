package com.project.test.account.service;

import com.project.test.account.dto.AccountDto;
import com.project.test.account.mapper.AccountMapper;
import com.project.test.account.repository.AccountRepository;
import com.project.test.client.repository.ClientRepository;
import com.project.test.exception.ClientNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

import static com.project.test.exception.Message.CLIENT_NOT_FOUND;

@Service
@RequiredArgsConstructor
@Slf4j
public class AccountServiceImpl implements AccountService {
    private final AccountRepository accountRepository;
    private final ClientRepository clientRepository;

    @Override
    public List<AccountDto> getAccountsByClientId(Long clientId) {
        log.info("AccountService getAccountsByClientId clientId={}", clientId);
        checkClientExists(clientId);

        return accountRepository.findAllByClientId(clientId).stream()
                .peek(obj -> log.info("Account with id={} has been found!", obj.getId()))
                .map(AccountMapper.INSTANCE::toDto)
                .collect(Collectors.toList());
    }

    private void checkClientExists(Long clientId) {
        if (Boolean.FALSE.equals(clientRepository.existsById(clientId))) {
            log.warn("Client with id={} has not been found!", clientId);
            throw new ClientNotFoundException(String.format(CLIENT_NOT_FOUND.get(), clientId));
        }
    }
}
