package com.it355pz2.entity;

import java.io.Serializable;
import java.util.Objects;

public class UserSkillId implements Serializable {
    private Long userId;
    private Long skillId;

    // Default constructor required by Hibernate
    public UserSkillId() {
    }

    public UserSkillId(Long userId, Long skillId) {
        this.userId = userId;
        this.skillId = skillId;
    }

    // Getters and setters required by Hibernate
    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public Long getSkillId() {
        return skillId;
    }

    public void setSkillId(Long skillId) {
        this.skillId = skillId;
    }

    @Override
    public int hashCode() {
        return Objects.hash(userId, skillId);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (!(obj instanceof UserSkillId that)) return false;
        return Objects.equals(this.userId, that.userId) && Objects.equals(this.skillId, that.skillId);
    }
}
