package com.project.test.cashorder.controller;

import com.project.test.cashorder.dto.CashOrderDto;
import com.project.test.cashorder.dto.NewCashOrderRequest;
import com.project.test.cashorder.service.CashOrderService;
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
@RequestMapping("/api/v1/cashorders")
public class CashOrderController {
    private static final String ENDPOINT_GET_ORDERS_BY_ACCOUNT_NUMBER = "/accounts/{accountNumber}";
    private final CashOrderService cashOrderService;

    @GetMapping(ENDPOINT_GET_ORDERS_BY_ACCOUNT_NUMBER)
    public List<CashOrderDto> getCashOrdersByAccountNumber(@PositiveOrZero @PathVariable Long accountNumber) {
        log.info("GET /api/v1/cashorders/accounts/{}", accountNumber);
        return cashOrderService.getCashOrdersByAccountNumber(accountNumber);
    }

    /**
     * For withdrawing and depositing funds from/to account
     **/
    @PostMapping
    public CashOrderDto createCashOrder(@RequestHeader("Secret-Word") String secretWord,
                                        @Valid @RequestBody NewCashOrderRequest cashOrderReq) {
        log.info("POST /api/v1/cashorders");
        return cashOrderService.createCashOrder(cashOrderReq, secretWord);
    }
}
