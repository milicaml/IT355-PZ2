package com.it355pz2.dto;

import lombok.Data;

@Data
public class AddUserSkillRequest {
    private Long skillId;
    private String proficiencyLevel;
}
