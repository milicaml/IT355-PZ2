package com.it355pz2.entity.enums;

import lombok.Getter;

@Getter
public enum ProficiencyLevel {
    BEGINNER("beginner"),
    INTERMEDIATE("intermediate"),
    ADVANCED("advanced");

    private final String level;

    ProficiencyLevel(String level) {
        this.level = level;
    }
}