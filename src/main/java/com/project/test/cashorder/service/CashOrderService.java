package com.project.test.cashorder.service;

import com.project.test.cashorder.dto.CashOrderDto;
import com.project.test.cashorder.dto.NewCashOrderRequest;

import java.util.List;

public interface CashOrderService {
    List<CashOrderDto> getCashOrdersByAccountNumber(Long accountNumber);

    CashOrderDto createCashOrder(NewCashOrderRequest cashOrderReq, String secretWord);
}
