package com.playback.devready.repository;

import com.playback.devready.model.RoleSkill;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface RoleSkillRepository extends JpaRepository<RoleSkill, Long> {
    List<RoleSkill> findByRoleId(Long roleId);
}
