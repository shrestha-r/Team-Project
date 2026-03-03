package com.playback.devready.model;

import jakarta.persistence.*;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "skills")
public class Skill {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String name;

    @Column(nullable = false)
    private String category;

    @Column(name = "default_importance", nullable = false)
    private Integer defaultImportance;

    @OneToMany(mappedBy = "skill")
    private List<RoleSkill> roleSkills = new ArrayList<>();

    @OneToMany(mappedBy = "skill")
    private List<UserSkill> userSkills = new ArrayList<>();

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getCategory() { return category; }
    public void setCategory(String category) { this.category = category; }
    public Integer getDefaultImportance() { return defaultImportance; }
    public void setDefaultImportance(Integer defaultImportance) { this.defaultImportance = defaultImportance; }
    public List<RoleSkill> getRoleSkills() { return roleSkills; }
    public void setRoleSkills(List<RoleSkill> roleSkills) { this.roleSkills = roleSkills; }
    public List<UserSkill> getUserSkills() { return userSkills; }
    public void setUserSkills(List<UserSkill> userSkills) { this.userSkills = userSkills; }
}
