package com.it355pz2.services.impl;

import com.it355pz2.dto.SkillResponse;
import com.it355pz2.entity.Skill;
import com.it355pz2.entity.UserSkill;
import com.it355pz2.entity.enums.ProficiencyLevel;
import com.it355pz2.repository.SkillRepository;
import com.it355pz2.repository.UserSkillRepository;
import com.it355pz2.services.SkillService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.ArrayList;
import java.util.stream.Collectors;

@AllArgsConstructor
@Service
public class SkillServiceImpl implements SkillService {
    private SkillRepository skillRepository;
    private UserSkillRepository userSkillRepository;

    @Override
    public List<SkillResponse> getSkills() {
        List<Skill> skills = skillRepository.findAll();
        return skills.stream().map(SkillResponse::new).toList();
    }

    @Override
    public List<SkillResponse> getUserSkills(Long userId) {
        List<UserSkill> userSkills = userSkillRepository.findByUserId(userId);

        return userSkills.stream()
                .filter(userSkill -> userSkill.getSkill() != null) // Filter out null skills
                .map(userSkill -> new SkillResponse(userSkill.getSkill(), userSkill.getLevel()))
                .collect(Collectors.toList());
    }

    @Override
    public SkillResponse addUserSkill(Long userId, Long skillId, String proficiencyLevel) {
        Skill skill = skillRepository.findById(skillId)
                .orElseThrow(() -> new RuntimeException("Skill not found"));

        UserSkill userSkill = new UserSkill();
        userSkill.setUserId(userId);
        userSkill.setSkillId(skillId);
        userSkill.setLevel(ProficiencyLevel.fromString(proficiencyLevel));

        userSkillRepository.save(userSkill);

        return new SkillResponse(skill, userSkill.getLevel());
    }

    @Override
    public SkillResponse updateUserSkill(Long userId, Long skillId, String proficiencyLevel) {
        UserSkill userSkill = userSkillRepository.findByUserIdAndSkillId(userId, skillId)
                .orElseThrow(() -> new RuntimeException("User skill not found"));

        userSkill.setLevel(ProficiencyLevel.fromString(proficiencyLevel));
        userSkillRepository.save(userSkill);

        Skill skill = skillRepository.findById(skillId)
                .orElseThrow(() -> new RuntimeException("Skill not found"));

        return new SkillResponse(skill, userSkill.getLevel());
    }

    @Override
    public void removeUserSkill(Long userId, Long skillId) {
        UserSkill userSkill = userSkillRepository.findByUserIdAndSkillId(userId, skillId)
                .orElseThrow(() -> new RuntimeException("User skill not found"));

        userSkillRepository.delete(userSkill);
    }
}
