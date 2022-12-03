package com.project.test.transaction.dto;

import com.project.test.transaction.enums.TransactMessage;
import com.project.test.transaction.enums.TransactionType;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;


import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class TransactionDto {
    private Long id;
    @DateTimeFormat(pattern = "uuuu-MM-dd hh:mm:ss")
    private LocalDateTime createdAt;
    private String transactionType;
    private Long cashOrderId;
    private Long accountId;
    private BigDecimal sum;
    private Boolean isSuccessful;
    private String message;
}
