package com.project.test.transaction.enums;

public enum TransactionType {
    DEPOSIT("Deposit"),
    WITHDRAWAL("Withdrawal"),
    TRANSFER("Transfer");

    private final String text;
    TransactionType(String text) {
        this.text = text;
    }

    public String get() {
        return text;
    }
}
