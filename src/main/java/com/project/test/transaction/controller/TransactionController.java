package com.project.test.transaction.controller;

import com.project.test.transaction.dto.InnerTransactionRequestDto;
import com.project.test.transaction.dto.OuterTransactionRequestDto;
import com.project.test.transaction.dto.TransactionDto;
import com.project.test.transaction.service.TransactionService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@Slf4j
@RequiredArgsConstructor
@Validated
@RequestMapping("/api/v1/transactions")
public class TransactionController {

    private static final String GET_TRANSACTIONS_BY_ACCOUNT_NUMBER = "/accounts/{accountNumber}";
    private static final String POST_INNER_TRANSACTION = "/inner";
    private static final String POST_OUTER_TRANSACTION = "/outer";
    private final TransactionService transactionService;

    @GetMapping(GET_TRANSACTIONS_BY_ACCOUNT_NUMBER)
    public List<TransactionDto> getTransactionsByAccountNumber(@PositiveOrZero @PathVariable Long accountNumber) {
        log.info("GET /api/v1/transactions/accounts/{accountNumber}");
        return transactionService.getTransactionsByAccountNumber(accountNumber);
    }

    /**
     * Conducts inner transaction between two accounts of one client
     */
    @PostMapping(POST_INNER_TRANSACTION)
    public TransactionDto createInnerTransaction(@RequestHeader("Secret-Word") String secretWord,
                                                 @Valid @RequestBody InnerTransactionRequestDto transactionRequest) {
        log.info("POST /api/v1/transactions/inner");
        return transactionService.createInnerTransaction(transactionRequest, secretWord);
    }

    /**
     * Conducts outer transaction between client account and second-party account
     */
    @PostMapping(POST_OUTER_TRANSACTION)
    public TransactionDto createOuterTransaction(@RequestHeader("Secret-Word") String secretWord,
                                                 @Valid @RequestBody OuterTransactionRequestDto transactionRequest) {
        log.info("POST /api/v1/transactions/outer");
        return transactionService.createOuterTransaction(transactionRequest, secretWord);
    }

}
