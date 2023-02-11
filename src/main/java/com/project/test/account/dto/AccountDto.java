package com.project.test.account.dto;

import com.project.test.account.enums.AccountType;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Date;

@Data
public class AccountDto {
    private Long id;
    private Long clientId;
    private Long number;
    private BigDecimal sum;
    private AccountType type;
    @DateTimeFormat(pattern = "uuuu-MM-dd hh:mm:ss")
    private LocalDateTime registrationDate;
    @DateTimeFormat(pattern = "uuuu-MM-dd hh:mm:ss")
    private LocalDateTime expirationDate;
}
