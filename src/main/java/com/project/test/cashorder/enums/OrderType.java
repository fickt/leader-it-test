package com.project.test.cashorder.enums;

public enum OrderType {
    WITHDRAWAL("Withdrawal"),
    DEPOSIT("Deposit");

    private final String text;

    OrderType(String text) {
        this.text = text;
    }
    public String getValue() {
        return text;
    }
}
