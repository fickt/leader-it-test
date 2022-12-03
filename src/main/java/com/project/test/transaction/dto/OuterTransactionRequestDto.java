package com.project.test.transaction.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.math.BigDecimal;

/**
 * Contains all necessary info for conducting outer transaction
 *
 * clientId - in order to make sure that client is owner of account from which funds will be taken
 * fromAccountNumber - account from which funds will be taken
 * toAccountNumber - account to which funds will be transferred
 * sum - sum to transfer
 */
@Data
public class OuterTransactionRequestDto {
    @NotNull(message = "client id should not be empty")
    private Long clientId;
    @NotNull(message = "from Account Number should not be empty")
    private Long fromAccountNumber;
    @NotNull(message = "to Account Number should not be empty")
    private Long toAccountNumber;
    @NotNull(message = "sum should not be empty")
    private BigDecimal sum;
}