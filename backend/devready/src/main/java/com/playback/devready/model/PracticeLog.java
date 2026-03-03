package com.playback.devready.model;

import jakarta.persistence.*;

import java.time.LocalDate;

@Entity
@Table(name = "practice_logs")
public class PracticeLog {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional = false)
    @JoinColumn(name = "user_skill_id", nullable = false)
    private UserSkill userSkill;

    @Column(nullable = false)
    private Integer minutes;

    @Column(name = "practice_date", nullable = false)
    private LocalDate practiceDate;

    @Column(columnDefinition = "TEXT")
    private String notes;

    @PrePersist
    public void prePersist() {
        if (practiceDate == null) practiceDate = LocalDate.now();
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public UserSkill getUserSkill() { return userSkill; }
    public void setUserSkill(UserSkill userSkill) { this.userSkill = userSkill; }
    public Integer getMinutes() { return minutes; }
    public void setMinutes(Integer minutes) { this.minutes = minutes; }
    public LocalDate getPracticeDate() { return practiceDate; }
    public void setPracticeDate(LocalDate practiceDate) { this.practiceDate = practiceDate; }
    public String getNotes() { return notes; }
    public void setNotes(String notes) { this.notes = notes; }
}
