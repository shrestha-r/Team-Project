package com.playback.devready.dto;

public record PlanItemResponse(Long userSkillId, Long skillId, String skill, Integer minutes, Double urgency) {
}
