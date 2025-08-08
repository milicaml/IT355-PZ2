package com.it355pz2.repository;

import com.it355pz2.entity.UserSkill;
import com.it355pz2.entity.UserSkillId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserSkillRepository extends JpaRepository<UserSkill, UserSkillId> {
}
