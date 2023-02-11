package com.project.test.transaction.model;

import com.project.test.transaction.enums.TransactMessage;
import com.project.test.transaction.enums.TransactionType;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.format.annotation.DateTimeFormat;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Table(name = "TRANSACTION_TABLE")
public class Transaction {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @DateTimeFormat(pattern = "uuuu-MM-dd hh:mm:ss")
    @Column(name = "CREATED_AT")
    private LocalDateTime createdAt;
    @Column(name = "TRANSACTION_TYPE")
    @Enumerated(EnumType.STRING)
    private TransactionType transactionType;
    @Column(name = "FK_CASHORDER_ID")
    private Long cashOrderId;
    @Column(name = "FK_ACCOUNT_ID")
    private Long accountId;
    @Column(name = "SUM")
    private BigDecimal sum;
    @Column(name = "IS_SUCCESSFUL")
    private Boolean isSuccessful;
    @Column(name = "MESSAGE")
    @Enumerated(EnumType.STRING)
    private TransactMessage message;
}
