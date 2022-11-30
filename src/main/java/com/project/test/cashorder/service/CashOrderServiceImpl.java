package com.project.test.cashorder.service;

import at.favre.lib.crypto.bcrypt.BCrypt;
import com.project.test.account.model.Account;
import com.project.test.account.repository.AccountRepository;
import com.project.test.cashorder.dto.CashOrderDto;
import com.project.test.cashorder.dto.NewCashOrderRequest;
import com.project.test.cashorder.enums.OrderMessage;
import com.project.test.cashorder.enums.OrderType;
import com.project.test.cashorder.mapper.CashOrderMapper;
import com.project.test.cashorder.model.CashOrder;
import com.project.test.cashorder.repository.CashOrderRepository;
import com.project.test.client.model.Client;
import com.project.test.client.repository.ClientRepository;
import com.project.test.exception.*;
import com.project.test.transaction.enums.TransactMessage;
import com.project.test.transaction.model.Transaction;
import com.project.test.transaction.repository.TransactionRepository;
import com.project.test.transaction.enums.TransactionType;
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
@RequiredArgsConstructor
@Slf4j
public class CashOrderServiceImpl implements CashOrderService {

    private static final String LOG_WRONG_SECRET_WORD = "Wrong secret word={}";
    private static final String LOG_INSUFFICIENT_FUNDS = "Inadequate sum to withdraw, account balance={}, sum to withdraw={}";
    private static final String LOG_UNKNOWN_OPERATION = "Unknown operation type={}";
    private static final String LOG_VALIDATION_SUCCESS = "Validation passed successfully!";
    private static final String LOG_CHOSEN_OPERATION = "Chosen operation type={}";
    private static final String LOG_INVALID_SUM = "Sum can not be negative! Sum={}";

    private final CashOrderRepository cashOrderRepository;
    private final AccountRepository accountRepository;
    private final ClientRepository clientRepository;
    private final TransactionRepository transactionRepository;

    @Override
    public List<CashOrderDto> getCashOrdersByAccountNumber(Long accountNumber) {
        Account account = accountRepository.findByNumber(accountNumber)
                .orElseThrow(() -> new AccountNotFoundException(String.format(ACCOUNT_BY_NUMBER_NOT_FOUND.get(), accountNumber)));

        return cashOrderRepository.findAllByAccountId(account.getId()).stream()
                .peek(obj -> log.info("Cash order with id={} has been found by accountNumber={}!", obj.getId(), accountNumber))
                .map(CashOrderMapper.INSTANCE::toDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(
            isolation = Isolation.READ_COMMITTED,
            propagation = Propagation.REQUIRED,
            noRollbackFor = {WrongSecretWordException.class, InadequateSumToWithdrawException.class}
    )
    public CashOrderDto createCashOrder(NewCashOrderRequest cashOrderReq, String secretWord) {
        log.info("Creating cash order...");
        Account account = getAccountById(cashOrderReq.getAccountId());
        Client client = getClientById(account.getClientId());

        if (cashOrderReq.getSum().compareTo(BigDecimal.valueOf(0)) < 0) {
            log.warn(LOG_INVALID_SUM, cashOrderReq.getSum());
            throw new InvalidSumException(INVALID_SUM.get());
        }

        if (cashOrderReq.getOrderType().equalsIgnoreCase(OrderType.WITHDRAWAL.getValue())) { // if user chooses withdrawal operation
            log.info(LOG_CHOSEN_OPERATION, OrderType.WITHDRAWAL.getValue());

            if (!validateSecretWord(secretWord, client.getSecretWord())) { //validates secret word
                log.warn(LOG_WRONG_SECRET_WORD, secretWord);
                CashOrder persistedCashOrder = createFailedCashOrder(cashOrderReq.getSum(), // if check fails, create failed cash order
                        account.getId(),
                        OrderType.WITHDRAWAL,
                        OrderMessage.FAIL_WRONG_SECRET_WORD);

                createTransaction(persistedCashOrder, //creates transaction based on info from failed order
                        Boolean.FALSE,
                        TransactionType.WITHDRAWAL,
                        TransactMessage.FAIL_WRONG_SECRET_WORD);
                throw new WrongSecretWordException(String.format(WRONG_SECRET_WORD.get(), secretWord));
            }

            if (!validateRequestSum(account.getSum(), cashOrderReq.getSum())) { // check if account sum < request sum then throw exception
                log.warn(LOG_INSUFFICIENT_FUNDS,
                        account.getSum(),
                        cashOrderReq.getSum());

                CashOrder persistedCashOrder = createFailedCashOrder(cashOrderReq.getSum(),// if check fails, create failed cash order
                        account.getId(),
                        OrderType.WITHDRAWAL,
                        OrderMessage.FAIL_WITHDRAW_INADEQUATE_SUM);

                createTransaction(persistedCashOrder, //creates transaction based on info from failed order
                        Boolean.FALSE,
                        TransactionType.WITHDRAWAL,
                        TransactMessage.FAIL_WITHDRAW_INADEQUATE_SUM);

                throw new InadequateSumToWithdrawException(String.format(INADEQUATE_SUM_TO_WITHDRAW.get(),
                        account.getSum(),
                        cashOrderReq.getSum()));
            }
            // **all checks passed successfully**
            log.info(LOG_VALIDATION_SUCCESS);
            CashOrder persistedCashOrder = createSuccessfulCashOrder(account, //creates successful cash order
                    cashOrderReq,
                    OrderType.WITHDRAWAL);

            createTransaction(persistedCashOrder, //creates transaction based on info from successful order
                    Boolean.TRUE,
                    TransactionType.WITHDRAWAL,
                    TransactMessage.SUCCESS);

            return CashOrderMapper.INSTANCE.toDto(persistedCashOrder); //converting to DTO via MapStruct

        } else if (cashOrderReq.getOrderType().equalsIgnoreCase(OrderType.DEPOSIT.getValue())) { // if user chooses deposit operation
            log.info(LOG_CHOSEN_OPERATION, OrderType.DEPOSIT.getValue());
            if (!validateSecretWord(secretWord, client.getSecretWord())) { //validates secret word
                log.warn(LOG_WRONG_SECRET_WORD, secretWord);
                CashOrder persistedCashOrder = createFailedCashOrder(cashOrderReq.getSum(), // if check fails, create failed cash order
                        account.getId(),
                        OrderType.DEPOSIT,
                        OrderMessage.FAIL_WRONG_SECRET_WORD);

                createTransaction(persistedCashOrder, //creates transaction based on info from failed order
                        Boolean.FALSE,
                        TransactionType.DEPOSIT,
                        TransactMessage.FAIL_WRONG_SECRET_WORD);

                throw new WrongSecretWordException(String.format(WRONG_SECRET_WORD.get(), secretWord));
            }
            // **all checks passed successfully**
            log.info(LOG_VALIDATION_SUCCESS);
            CashOrder persistedCashOrder = createSuccessfulCashOrder(account,  //creates successful cash order
                    cashOrderReq,
                    OrderType.DEPOSIT);

            createTransaction(persistedCashOrder, //creates transaction based on info from successful order
                    Boolean.TRUE,
                    TransactionType.DEPOSIT,
                    TransactMessage.SUCCESS);
            return CashOrderMapper.INSTANCE.toDto(persistedCashOrder); //converting to DTO via MapStruct
        } else {
            log.warn(LOG_UNKNOWN_OPERATION, cashOrderReq.getOrderType());
            throw new UnsupportedOperationException(String.format(UNSUPPORTED_OPERATION.get(), cashOrderReq.getOrderType()));
        }
    }

    /**
     * @param account      - account with which we conduct chosen operation
     * @param orderRequest - we will get value of field sum from it
     * @param orderType    - order type, e.g withdrawal, deposit, etc
     * @return {@link CashOrder} with all information needed for creating successful transaction
     */
    @Transactional(
            isolation = Isolation.READ_COMMITTED,
            propagation = Propagation.MANDATORY,
            noRollbackFor = {WrongSecretWordException.class, InadequateSumToWithdrawException.class}
    )
    protected CashOrder createSuccessfulCashOrder(Account account, NewCashOrderRequest orderRequest, OrderType orderType) {
        if (orderType.equals(OrderType.WITHDRAWAL)) {
            account.setSum(account.getSum().subtract(orderRequest.getSum()));
        } else {
            account.setSum(account.getSum().add(orderRequest.getSum()));
        }
        accountRepository.save(account);

        CashOrder cashOrder = new CashOrder();
        cashOrder.setSum(orderRequest.getSum());
        cashOrder.setCreatedAt(LocalDateTime.now().truncatedTo(ChronoUnit.SECONDS));
        cashOrder.setIsSuccessful(Boolean.TRUE);
        cashOrder.setMessage(OrderMessage.SUCCESS);
        cashOrder.setOrderType(orderType);
        cashOrder.setAccountId(account.getId());

        return cashOrderRepository.save(cashOrder);
    }

    /**
     * @param cashOrder    - transaction gets from it info like cashOrderId, cashOrderSum
     * @param isSuccessful - if transaction fails/succeeds, it should be false/true respectively
     * @param type         - transaction type, e.g withdrawal, deposit, etc
     * @param message      - if transaction fails, there should be reason, if it is successful, just put here TransactMessage.SUCCEED
     */
    @Transactional(
            isolation = Isolation.READ_COMMITTED,
            propagation = Propagation.MANDATORY,
            noRollbackFor = {WrongSecretWordException.class, InadequateSumToWithdrawException.class}
    )
    protected void createTransaction(CashOrder cashOrder, Boolean isSuccessful, TransactionType type, TransactMessage message) {
        Transaction transaction = new Transaction();
        transaction.setSum(cashOrder.getSum());
        transaction.setIsSuccessful(isSuccessful);
        transaction.setCreatedAt(LocalDateTime.now().truncatedTo(ChronoUnit.SECONDS));
        transaction.setCashOrderId(cashOrder.getId());
        transaction.setTransactionType(type);
        transaction.setMessage(message);
        transactionRepository.save(transaction);
    }

    /**
     * @param sum          - funds we tried deposit or withdraw
     * @param accountId    - account id we tried to work with
     * @param orderType    - order type, e.g withdrawal, deposit, etc
     * @param orderMessage - if cashOrder fails, there should be reason, if it is successful, just put here OrderMessage.SUCCEED
     * @return {@link CashOrder} with all information needed for creating failed transaction
     */
    @Transactional(
            isolation = Isolation.READ_COMMITTED,
            propagation = Propagation.MANDATORY,
            noRollbackFor = {WrongSecretWordException.class, InadequateSumToWithdrawException.class}
    )
    protected CashOrder createFailedCashOrder(BigDecimal sum, Long accountId, OrderType orderType, OrderMessage orderMessage) {
        CashOrder failedCashOrder = new CashOrder();
        failedCashOrder.setCreatedAt(LocalDateTime.now().truncatedTo(ChronoUnit.SECONDS));
        failedCashOrder.setIsSuccessful(Boolean.FALSE);
        failedCashOrder.setAccountId(accountId);
        failedCashOrder.setOrderType(orderType);
        failedCashOrder.setSum(sum);
        failedCashOrder.setMessage(orderMessage);

        return cashOrderRepository.save(failedCashOrder);
    }

    /**
     * @param accountId - account id by which we want to find bank account
     * @return {@link Account} bank account which is found by given account id
     * @throws AccountNotFoundException if bank account with given id does not exist
     */
    private Account getAccountById(Long accountId) {
        return accountRepository.findById(accountId)
                .orElseThrow(() -> new AccountNotFoundException(String.format(ACCOUNT_BY_ID_NOT_FOUND.get(), accountId)));
    }

    /**
     * @param clientId - client id by which we want to find client
     * @return {@link Client} client which is found by given client id
     * @throws ClientNotFoundException if client with given id does not exist
     */
    private Client getClientById(Long clientId) {
        return clientRepository.findById(clientId)
                .orElseThrow(() -> new ClientNotFoundException(String.format(CLIENT_NOT_FOUND.get(), clientId)));
    }

    /**
     * @param actualAccountSum - sum we currently have on bank account
     * @param sumToWithdraw    - sum we try to withdraw from bank account
     * @return false if sumToWithDraw is greater than actualAccountSum, and true vice versa
     */
    private boolean validateRequestSum(BigDecimal actualAccountSum, BigDecimal sumToWithdraw) {
        return !(sumToWithdraw.compareTo(actualAccountSum) > 0);
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
}
