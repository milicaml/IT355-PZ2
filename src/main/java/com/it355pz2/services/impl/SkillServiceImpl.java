package com.it355pz2.services.impl;

import com.it355pz2.dto.SkillResponse;
import com.it355pz2.entity.Skill;
import com.it355pz2.repository.SkillRepository;
import com.it355pz2.services.SkillService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@AllArgsConstructor
@Service
public class SkillServiceImpl implements SkillService {
    private SkillRepository skillRepository;
    @Override
    public List<SkillResponse> getSkills() {
        List<Skill> skills = skillRepository.findAll();
        return skills.stream().map(SkillResponse::new).toList();
//        return skills.stream().map(e -> new SkillResponse(e)).toList();
    }
}
