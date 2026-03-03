package com.playback.devready.model;

import jakarta.persistence.*;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "user_skills", uniqueConstraints = @UniqueConstraint(name = "uk_user_skill", columnNames = {"user_id", "skill_id"}))
public class UserSkill {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional = false)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne(optional = false)
    @JoinColumn(name = "skill_id", nullable = false)
    private Skill skill;

    @Column(nullable = false)
    private Integer confidence;

    @Column(name = "last_practiced", nullable = false)
    private LocalDate lastPracticed;

    @Column(name = "custom_importance")
    private Integer customImportance;

    @OneToMany(mappedBy = "userSkill")
    private List<PracticeLog> practiceLogs = new ArrayList<>();

    @PrePersist
    public void prePersist() {
        if (confidence == null) confidence = 5;
        if (lastPracticed == null) lastPracticed = LocalDate.now();
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public User getUser() { return user; }
    public void setUser(User user) { this.user = user; }
    public Skill getSkill() { return skill; }
    public void setSkill(Skill skill) { this.skill = skill; }
    public Integer getConfidence() { return confidence; }
    public void setConfidence(Integer confidence) { this.confidence = confidence; }
    public LocalDate getLastPracticed() { return lastPracticed; }
    public void setLastPracticed(LocalDate lastPracticed) { this.lastPracticed = lastPracticed; }
    public Integer getCustomImportance() { return customImportance; }
    public void setCustomImportance(Integer customImportance) { this.customImportance = customImportance; }
    public List<PracticeLog> getPracticeLogs() { return practiceLogs; }
    public void setPracticeLogs(List<PracticeLog> practiceLogs) { this.practiceLogs = practiceLogs; }
}
