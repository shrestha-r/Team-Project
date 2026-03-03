package com.playback.devready.model;

import jakarta.persistence.*;

@Entity
@Table(name = "role_skills", uniqueConstraints = @UniqueConstraint(name = "uk_role_skill", columnNames = {"role_id", "skill_id"}))
public class RoleSkill {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional = false)
    @JoinColumn(name = "role_id", nullable = false)
    private Role role;

    @ManyToOne(optional = false)
    @JoinColumn(name = "skill_id", nullable = false)
    private Skill skill;

    @Column(name = "importance_weight", nullable = false)
    private Integer importanceWeight;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public Role getRole() { return role; }
    public void setRole(Role role) { this.role = role; }
    public Skill getSkill() { return skill; }
    public void setSkill(Skill skill) { this.skill = skill; }
    public Integer getImportanceWeight() { return importanceWeight; }
    public void setImportanceWeight(Integer importanceWeight) { this.importanceWeight = importanceWeight; }
}
