package com.it355pz2.entity.enums;

import lombok.Getter;

@Getter
public enum ApplicationStatus {
    pending("pending"),
    accepted("accepted"),
    rejected("rejected");

    private final String value;

    ApplicationStatus(String value) {
        this.value = value;
    }
}
