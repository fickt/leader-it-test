package com.project.test.exception;

public enum Message {
    CLIENT_NOT_FOUND("Client with id=%s has not been found!"),
    ACCOUNT_BY_NUMBER_NOT_FOUND("Account with number=%s has not been found!"),
    ACCOUNT_BY_ID_NOT_FOUND("Account with id=%s has not been found!"),
    INADEQUATE_SUM_TO_WITHDRAW("Insufficient funds! Actual account balance=%s, attempt to withdraw sum=%s"),
    INADEQUATE_SUM_TO_TRANSFER("Insufficient funds! Actual account balance=%s, attempt to transfer sum=%s"),
    WRONG_SECRET_WORD("Invalid secret word=%s"),
    NOT_ACCOUNT_OWNER("You are not owner of account=%s"),
    INVALID_SUM("Sum can not be negative!"),
    UNSUPPORTED_OPERATION("Unsupported operation=%s");
    private final String text;
    Message(String text) {
        this.text = text;
    }

    public String get() {
        return text;
    }
}
