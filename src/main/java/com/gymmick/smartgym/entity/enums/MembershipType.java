package com.gymmick.smartgym.entity.enums;

public enum MembershipType {
    WEEKLY,
    MONTHLY,
    YEARLY;

    public static boolean isValid(String value) {
        if (value == null) {
            return false;
        }

        for (MembershipType membershipType : MembershipType.values()) {
            if (membershipType.name().equalsIgnoreCase(value)) {
                return true;
            }
        }

        return false;
    }
}
