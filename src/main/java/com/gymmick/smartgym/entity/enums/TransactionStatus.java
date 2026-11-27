package com.gymmick.smartgym.entity.enums;

public enum TransactionStatus {
    PENDING,
    PROCESS,
    SUCCESS,
    FAILURE,
    EXPIRED,
    REFUNDED,
    CANCELLED,
    CHALLENGE;

    public static boolean isValid(String value) {
        if (value == null) {
            return false;
        }

        for (TransactionStatus transactionStatus : TransactionStatus.values()) {
            if (transactionStatus.name().equalsIgnoreCase(value)) {
                return true;
            }
        }

        return false;
    }
}
