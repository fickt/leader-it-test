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
    @NotNull
    private Long clientId;
    @NotNull
    private Long fromAccountNumber;
    @NotNull
    private Long toAccountNumber;
    @NotNull
    private BigDecimal sum;
}