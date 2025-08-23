package com.it355pz2.services;


import com.it355pz2.dto.SkillResponse;

import java.util.List;

public interface SkillService {
    List<SkillResponse> getSkills();
    
    // User skills methods
    List<SkillResponse> getUserSkills(Long userId);
    SkillResponse addUserSkill(Long userId, Long skillId, String proficiencyLevel);
    SkillResponse updateUserSkill(Long userId, Long skillId, String proficiencyLevel);
    void removeUserSkill(Long userId, Long skillId);
}
