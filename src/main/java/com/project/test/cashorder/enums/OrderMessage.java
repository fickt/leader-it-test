package com.project.test.cashorder.enums;

public enum OrderMessage {
    SUCCESS("Completed successfully"),
    FAIL_WRONG_SECRET_WORD("Wrong secret word"),
    FAIL_WITHDRAW_INADEQUATE_SUM("Insufficient funds");


    private final String text;
    OrderMessage(String text) {
        this.text = text;
    }

    public String getValue() {
        return text;
    }
}
