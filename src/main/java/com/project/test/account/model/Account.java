package com.project.test.account.model;

import com.project.test.account.enums.AccountType;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.format.annotation.DateTimeFormat;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Date;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Table(name = "ACCOUNT_TABLE")
public class Account {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(name = "FK_CLIENT_ID")
    private Long clientId;
    @Column(name = "NUMBER")
    private Long number;
    @Column(name = "SUM")
    private BigDecimal sum;
    @Column(name = "ACCOUNT_TYPE")
    @Enumerated(EnumType.STRING)
    private AccountType type;
    @Column(name = "REGISTRATION_DATE")
    @DateTimeFormat(pattern = "uuuu-MM-dd hh:mm:ss")
    private LocalDateTime registrationDate;
    @Column(name = "EXPIRATION_DATE")
    @DateTimeFormat(pattern = "uuuu-MM-dd hh:mm:ss")
    private LocalDateTime expirationDate;
}
