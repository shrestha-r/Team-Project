package com.playback.devready.service;

import com.playback.devready.model.UserSkill;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

@Service
public class UrgencyEngineService {
    private final DeadlineService deadlineService;

    public UrgencyEngineService(DeadlineService deadlineService) {
        this.deadlineService = deadlineService;
    }

    public double calculateUrgency(UserSkill userSkill, LocalDate today) {
        long daysSince = Math.max(1, ChronoUnit.DAYS.between(userSkill.getLastPracticed(), today));
        int importanceWeight = resolveImportance(userSkill);
        double deadlineFactor = deadlineService.getFactor(userSkill.getUser(), userSkill.getSkill(), today);
        int confidence = Math.max(1, userSkill.getConfidence());

        double urgency = (daysSince * importanceWeight * deadlineFactor) / confidence;
        return Math.round(urgency * 100.0) / 100.0;
    }

    public int resolveImportance(UserSkill userSkill) {
        if (userSkill.getCustomImportance() != null) return userSkill.getCustomImportance();
        if (userSkill.getSkill() != null && userSkill.getSkill().getDefaultImportance() != null) {
            return userSkill.getSkill().getDefaultImportance();
        }
        return 1;
    }
}
