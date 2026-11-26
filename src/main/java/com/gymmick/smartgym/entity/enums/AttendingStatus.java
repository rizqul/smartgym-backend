package com.gymmick.smartgym.entity.enums;

public enum AttendingStatus {
    ACCEPTED,
    DECLINED;

    public static boolean isValid(String value) {
        if (value == null) {
            return false;
        }

        for (AttendingStatus attendingStatus : AttendingStatus.values()) {
            if (attendingStatus.name().equalsIgnoreCase(value)) {
                return true;
            }
        }

        return false;
    }
}
