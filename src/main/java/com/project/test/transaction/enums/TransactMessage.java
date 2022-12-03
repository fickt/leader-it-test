package com.project.test.transaction.enums;

public enum TransactMessage {
    SUCCESS("Completed successfully"),
    FAIL_WRONG_SECRET_WORD("Wrong secret word"),
    FAIL_TRANSFER_INADEQUATE_SUM("Insufficient funds!"),
    FAIL_INNER_TRANSACT_NOT_OWNER_ACCOUNT("Not owner of account to which funds are transferred!"),
    FAIL_WITHDRAW_INADEQUATE_SUM("Insufficient funds");


    private final String text;
    TransactMessage(String text) {
        this.text = text;
    }
    public String get() {
        return text;
    }
}
