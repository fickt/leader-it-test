package com.project.test.transaction.service;

import com.project.test.transaction.dto.InnerTransactionRequestDto;
import com.project.test.transaction.dto.OuterTransactionRequestDto;
import com.project.test.transaction.dto.TransactionDto;

import java.util.List;

public interface TransactionService {
    List<TransactionDto> getTransactionsByAccountNumber(Long accountNumber);

    TransactionDto createInnerTransaction(InnerTransactionRequestDto transactionRequest, String secretWord);

    TransactionDto createOuterTransaction(OuterTransactionRequestDto transactionRequest, String secretWord);
}
