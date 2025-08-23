package com.it355pz2.entity.enums;

import lombok.Getter;

@Getter
public enum ProficiencyLevel {
    BEGINNER("beginner"),
    INTERMEDIATE("intermediate"),
    ADVANCED("advanced"),
    EXPERT("expert");

    private final String level;

    ProficiencyLevel(String level) {
        this.level = level;
    }
    
    public static ProficiencyLevel fromString(String value) {
        if (value == null) {
            return BEGINNER;
        }
        
        String upperValue = value.toUpperCase();
        for (ProficiencyLevel level : values()) {
            if (level.name().equals(upperValue) || level.getLevel().equalsIgnoreCase(value)) {
                return level;
            }
        }
        return BEGINNER; // Default fallback
    }
}