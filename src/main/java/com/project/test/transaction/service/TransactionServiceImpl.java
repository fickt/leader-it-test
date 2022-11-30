package com.project.test.transaction.service;

import at.favre.lib.crypto.bcrypt.BCrypt;
import com.project.test.account.model.Account;
import com.project.test.account.repository.AccountRepository;
import com.project.test.client.model.Client;
import com.project.test.client.repository.ClientRepository;
import com.project.test.exception.*;
import com.project.test.transaction.dto.InnerTransactionRequestDto;
import com.project.test.transaction.dto.OuterTransactionRequestDto;
import com.project.test.transaction.dto.TransactionDto;
import com.project.test.transaction.enums.TransactMessage;
import com.project.test.transaction.enums.TransactionType;
import com.project.test.transaction.mapper.TransactionMapper;
import com.project.test.transaction.model.Transaction;
import com.project.test.transaction.repository.TransactionRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.stream.Collectors;

import static com.project.test.exception.Message.*;

@Service
@Slf4j
@RequiredArgsConstructor
public class TransactionServiceImpl implements TransactionService {

    private static final String LOG_WRONG_SECRET_WORD = "Wrong secret word={}";
    private static final String LOG_INSUFFICIENT_FUNDS = "Inadequate sum to transfer, account balance={}, sum to transfer={}";
    private static final String LOG_NOT_ACCOUNT_OWNER = "Not owner of account to which funds are transferred";
    private static final String LOG_VALIDATION_SUCCESS = "Validation passed successfully!";
    private static final String LOG_INVALID_SUM = "Sum can not be negative! Sum={}";

    private final TransactionRepository transactionRepository;
    private final AccountRepository accountRepository;
    private final ClientRepository clientRepository;

    @Override
    public List<TransactionDto> getTransactionsByAccountNumber(Long accountNumber) {
        log.info("Getting transactions by account number={}", accountNumber);
        Account account = getAccountByNumber(accountNumber);
        return transactionRepository.findAllByAccountId(account.getId()).stream()
                .peek(obj -> log.info("Transaction with id={} has been found!", obj.getId()))
                .map(TransactionMapper.INSTANCE::toDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(
            isolation = Isolation.READ_COMMITTED,
            propagation = Propagation.REQUIRED,
            noRollbackFor = {WrongSecretWordException.class,
                    InadequateSumToTransferException.class,
                    NotAccountOwnerException.class}
    )
    public TransactionDto createInnerTransaction(InnerTransactionRequestDto transactionRequest, String secretWord) {
        log.info("Creating inner transaction...");
        if (transactionRequest.getSum().compareTo(BigDecimal.valueOf(0)) < 0) {
            log.warn(LOG_INVALID_SUM, transactionRequest.getSum());
            throw new InvalidSumException(INVALID_SUM.get());
        }

        Client client = getClientById(transactionRequest.getClientId());
        Account fromAccount = getAccountByNumber(transactionRequest.getFromAccountNumber());

        if (!validateSecretWord(secretWord, client.getSecretWord())) {
            log.warn(LOG_WRONG_SECRET_WORD, secretWord);
            createFailedTransaction(fromAccount,
                    transactionRequest.getSum(),
                    TransactMessage.FAIL_WRONG_SECRET_WORD);

            throw new WrongSecretWordException(String.format(WRONG_SECRET_WORD.get(), secretWord));
        }

        if (!validateRequestSum(transactionRequest.getSum(), fromAccount.getSum())) {
            log.warn(LOG_INSUFFICIENT_FUNDS,
                    fromAccount.getSum(),
                    transactionRequest.getSum());

            createFailedTransaction(fromAccount,
                    transactionRequest.getSum(),
                    TransactMessage.FAIL_WITHDRAW_INADEQUATE_SUM);

            throw new InadequateSumToTransferException(String.format(INADEQUATE_SUM_TO_TRANSFER.get(),
                    fromAccount.getSum(),
                    transactionRequest.getSum()));
        }
        Account toAccount = getAccountByNumber(transactionRequest.getToAccountNumber());

        if (!toAccount.getClientId().equals(transactionRequest.getClientId())) {
            log.warn(LOG_NOT_ACCOUNT_OWNER);
            createFailedTransaction(fromAccount,
                    transactionRequest.getSum(),
                    TransactMessage.FAIL_INNER_TRANSACT_NOT_OWNER_ACCOUNT);
            throw new NotAccountOwnerException(String.format(NOT_ACCOUNT_OWNER.get(), toAccount.getNumber()));
        }
        log.info(LOG_VALIDATION_SUCCESS);
        Transaction successTransaction = conductTransfer(fromAccount,
                toAccount,
                transactionRequest.getSum());

        return TransactionMapper.INSTANCE.toDto(successTransaction);
    }


    @Override
    @Transactional(
            isolation = Isolation.READ_COMMITTED,
            propagation = Propagation.REQUIRED,
            noRollbackFor = {WrongSecretWordException.class, InadequateSumToTransferException.class}
    )
    public TransactionDto createOuterTransaction(OuterTransactionRequestDto transactionRequest, String secretWord) {
        log.info("Creating outer transaction...");
        Client client = getClientById(transactionRequest.getClientId());
        Account fromAccount = getAccountByNumber(transactionRequest.getFromAccountNumber());

        if (!validateSecretWord(secretWord, client.getSecretWord())) {
            log.info(LOG_WRONG_SECRET_WORD, secretWord);
            createFailedTransaction(fromAccount,
                    transactionRequest.getSum(),
                    TransactMessage.FAIL_WRONG_SECRET_WORD);
            throw new WrongSecretWordException(String.format(WRONG_SECRET_WORD.get(), secretWord));
        }

        if (!validateRequestSum(fromAccount.getSum(), transactionRequest.getSum())) {
            log.warn(LOG_INSUFFICIENT_FUNDS,
                    fromAccount.getSum(),
                    transactionRequest.getSum());

            createFailedTransaction(fromAccount,
                    transactionRequest.getSum(),
                    TransactMessage.FAIL_TRANSFER_INADEQUATE_SUM);
            throw new InadequateSumToTransferException(String.format(INADEQUATE_SUM_TO_TRANSFER.get(),
                    fromAccount.getSum(),
                    transactionRequest.getSum()));
        }
        log.info(LOG_VALIDATION_SUCCESS);

        Account toAccount = getAccountByNumber(transactionRequest.getToAccountNumber());
        Transaction successTransaction = conductTransfer(fromAccount,
                toAccount,
                transactionRequest.getSum());

        return TransactionMapper.INSTANCE.toDto(successTransaction);
    }

    /**
     * @param fromAccount - bank account from which funds are taken
     * @param toAccount   - bank account to which funds are transferred
     * @param sum         - sum to transfer
     * @return {@link Transaction}
     */
    @Transactional(
            isolation = Isolation.READ_COMMITTED,
            propagation = Propagation.MANDATORY,
            noRollbackFor = {WrongSecretWordException.class, InadequateSumToTransferException.class}
    )
    protected Transaction conductTransfer(Account fromAccount, Account toAccount, BigDecimal sum) {
        BigDecimal fromAccountResultSum = fromAccount.getSum().subtract(sum);
        BigDecimal toAccountResultSum = toAccount.getSum().add(sum);

        fromAccount.setSum(fromAccountResultSum);
        toAccount.setSum(toAccountResultSum);

        accountRepository.save(fromAccount);
        accountRepository.save(toAccount);

        Transaction successTransaction = new Transaction();
        successTransaction.setTransactionType(TransactionType.TRANSFER);
        successTransaction.setCreatedAt(LocalDateTime.now().truncatedTo(ChronoUnit.SECONDS));
        successTransaction.setMessage(TransactMessage.SUCCESS);
        successTransaction.setSum(sum);
        successTransaction.setIsSuccessful(Boolean.TRUE);
        successTransaction.setAccountId(fromAccount.getId());
        return transactionRepository.save(successTransaction);
    }

    private Account getAccountByNumber(Long accountNumber) {
        return accountRepository.findByNumber(accountNumber)
                .orElseThrow(() -> new AccountNotFoundException(String.format(ACCOUNT_BY_NUMBER_NOT_FOUND.get(), accountNumber)));
    }

    private Client getClientById(Long clientId) {
        return clientRepository.findById(clientId)
                .orElseThrow(() -> new ClientNotFoundException(String.format(CLIENT_NOT_FOUND.get(), clientId)));
    }

    /**
     * @param account         - account with which operation was failed
     * @param sum             - sum client tried to transfer
     * @param transactMessage - operation failure reason
     */
    private void createFailedTransaction(Account account, BigDecimal sum, TransactMessage transactMessage) {
        Transaction transaction = new Transaction();
        transaction.setTransactionType(TransactionType.TRANSFER);
        transaction.setCreatedAt(LocalDateTime.now().truncatedTo(ChronoUnit.SECONDS));
        transaction.setIsSuccessful(Boolean.FALSE);
        transaction.setSum(sum);
        transaction.setMessage(transactMessage);
        transaction.setAccountId(account.getId());
        transactionRepository.save(transaction);
    }


    /**
     * @param secretWord       - word client sent to service
     * @param actualSecretWord - actual word we have in database with which we will compare secretWord
     * @return false if secretWord is not equal to actualSecretWord and true vice versa
     */
    private boolean validateSecretWord(String secretWord, String actualSecretWord) {
        return BCrypt.verifyer()
                .verify(secretWord.getBytes(StandardCharsets.UTF_8),
                        actualSecretWord.getBytes(StandardCharsets.UTF_8))
                .verified;
    }

    /**
     * @param actualAccountSum - sum we currently have on bank account
     * @param sumToWithdraw    - sum we try to withdraw from bank account
     * @return true if sumToWithDraw is greater than actualAccountSum, and false vice versa
     */
    private boolean validateRequestSum(BigDecimal actualAccountSum, BigDecimal sumToWithdraw) {
        return !(sumToWithdraw.compareTo(actualAccountSum) > 0);
    }
}
