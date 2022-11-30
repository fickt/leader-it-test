package com.project.test.transaction.dto;


import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.math.BigDecimal;

/**
 * Contains all necessary info for conducting inner transaction
 *
 * clientId - in order to make sure that client is owner of both accounts as it is inner transaction
 * fromAccountNumber - account from which funds will be taken
 * toAccountNumber - account to which funds will be transferred
 * sum - sum to transfer
 */
@Data
public class InnerTransactionRequestDto {
    @NotNull(message = "client id should not be empty")
    private Long clientId;
    @NotNull(message = "from account should not be empty")
    private Long fromAccountNumber;
    @NotNull(message = "to account should not be empty")
    private Long toAccountNumber;
    @NotNull(message = "sum should not be empty")
    private BigDecimal sum;
}
