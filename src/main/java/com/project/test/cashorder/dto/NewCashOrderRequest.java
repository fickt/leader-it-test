package com.project.test.cashorder.dto;


import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class NewCashOrderRequest {
    @NotNull
    private Long accountId;
    @NotBlank
    private String orderType;
    @NotNull
    private BigDecimal sum;
}
