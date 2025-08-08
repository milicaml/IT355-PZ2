package com.it355pz2.entity.enums;

import lombok.Getter;

@Getter
public enum JobStatusType {
    open("open"),
    in_progress("in_progress"),
    completed("completed"),
    cancelled("cancelled");

    private final String value;

    JobStatusType(String value) {
        this.value = value;
    }
}