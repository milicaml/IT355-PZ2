package com.it355pz2.dto;

import com.it355pz2.entity.Skill;
import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class SkillResponse {
    private String title;

    public SkillResponse(Skill skill){
        this.title = skill.getTitle();
    }
}
