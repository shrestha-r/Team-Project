package com.playback.devready.dto;

import java.time.LocalDate;

public record UserSkillResponse(
        Long userSkillId,
        Long skillId,
        String skillName,
        String category,
        Integer confidence,
        LocalDate lastPracticed,
        Integer importance,
        Double urgency,
        Double healthScore,
        Double decayRate,
        Integer daysSince,
        Double daysToReminder,
        Double daysToCemetery,
        Integer relearnWeeks
) {
}
