package com.it355pz2.entity.enums;

import lombok.Getter;

@Getter
public enum JobType {
    full_time("full_time"),
    part_time("part_time"),
    contract("contract"),
    temporary("temporary");

    private final String value;

    JobType(String value) {
        this.value = value;
    }
}

