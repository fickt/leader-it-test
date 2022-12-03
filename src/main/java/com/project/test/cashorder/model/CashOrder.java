package com.project.test.cashorder.model;

import com.project.test.cashorder.enums.OrderMessage;
import com.project.test.cashorder.enums.OrderType;
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
@Table(name = "CASHORDER_TABLE")
public class CashOrder {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(name = "FK_ACCOUNT_ID")
    private Long accountId;
    @Column(name = "ORDER_TYPE")
    @Enumerated(EnumType.STRING)
    private OrderType orderType;
    @Column(name = "SUM")
    private BigDecimal sum;
    @Column(name = "IS_SUCCESSFUL")
    private Boolean isSuccessful;
    @DateTimeFormat(pattern = "uuuu-MM-dd hh:mm:ss")
    @Column(name = "CREATED_AT")
    private LocalDateTime createdAt;
    @Column(name = "MESSAGE")
    @Enumerated(EnumType.STRING)
    private OrderMessage message;
}
