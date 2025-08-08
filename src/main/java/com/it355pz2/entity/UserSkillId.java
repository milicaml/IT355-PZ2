package com.it355pz2.entity;

import java.io.Serializable;
import java.util.Objects;

public class UserSkillId implements Serializable {
    private final Long userId;
    private final Long skillId;

    public UserSkillId(Long userId, Long skillId) {
        this.userId = userId;
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
