package com.it355pz2.entity;

import com.it355pz2.entity.enums.ProficiencyLevel;
import com.it355pz2.entity.converter.ProficiencyLevelConverter;
import jakarta.persistence.*;
import lombok.Data;

@Entity
@Data
@Table(name = "user_skills")
@IdClass(UserSkillId.class)
public class UserSkill {
    @Id
    @Column(name = "user_id")
    private Long userId;

    @Id
    @Column(name = "skill_id")
    private Long skillId;

    @Column(name = "proficiency_level")
    @Convert(converter = ProficiencyLevelConverter.class)
    private ProficiencyLevel level;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", insertable = false, updatable = false)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "skill_id", insertable = false, updatable = false)
    private Skill skill;
}