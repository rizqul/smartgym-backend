package com.gymmick.smartgym.entity.enums;

public enum TransactionType {
    MEMBERSHIP,
    PT_BOOKING;

    public static boolean isValid(String value) {
        if (value == null) {
            return false;
        }

        for (TransactionType transactionType : TransactionType.values()) {
            if (transactionType.name().equalsIgnoreCase(value)) {
                return true;
            }
        }

        return false;
    }
}
