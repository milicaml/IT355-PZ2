package com.it355pz2.dto;

import com.it355pz2.entity.Skill;
import com.it355pz2.entity.enums.ProficiencyLevel;
import lombok.*;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class SkillResponse {
    private Long id;
    private String title;
    private ProficiencyLevel proficiencyLevel;
    
    public void setCategories(List<String> categories) {
        // This method is needed for JobResponse compatibility
    }

    public SkillResponse(Skill skill){
        this.id = skill.getId();
        this.title = skill.getTitle();
    }
    
    public SkillResponse(Skill skill, ProficiencyLevel proficiencyLevel){
        this.id = skill.getId();
        this.title = skill.getTitle();
        this.proficiencyLevel = proficiencyLevel != null ? proficiencyLevel : ProficiencyLevel.BEGINNER;
    }
}
