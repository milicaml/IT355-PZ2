package com.it355pz2.entity.enums;

import lombok.Getter;

@Getter
public enum UserType {
    employer("employer"),
    freelancer("freelancer"),
    admin("admin");

    private final String value;

    UserType(String value) {
        this.value = value;
    }

//    public static UserType valueOf(String value) {
//        for (UserType userType : UserType.values()) {
//            if (userType.value.equals(value)) {
//                return userType;
//            }
//        }
//        return null;
//    }
}