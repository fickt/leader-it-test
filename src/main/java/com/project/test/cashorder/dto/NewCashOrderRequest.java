package com.project.test.cashorder.dto;


import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class NewCashOrderRequest {
    @NotNull(message = "account id should not be empty")
    private Long accountId;
    @NotBlank(message = "order type should not be empty")
    private String orderType;
    @NotNull(message = "sum should not be empty")
    private BigDecimal sum;
}
