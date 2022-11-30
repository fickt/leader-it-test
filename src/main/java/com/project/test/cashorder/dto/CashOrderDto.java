package com.project.test.cashorder.dto;

import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class CashOrderDto {
    private Long id;
    private Long accountId;
    private String orderType;
    private BigDecimal sum;
    private Boolean isSuccessful;
    @DateTimeFormat(pattern = "uuuu-MM-dd hh:mm:ss")
    private LocalDateTime createdAt;
    private String message;
}
