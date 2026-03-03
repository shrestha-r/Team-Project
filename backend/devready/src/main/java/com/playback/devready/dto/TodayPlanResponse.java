package com.playback.devready.dto;

import java.time.LocalDate;
import java.util.List;

public record TodayPlanResponse(
        LocalDate date,
        Integer dailyLimit,
        Integer totalAllocated,
        Integer readinessScore,
        List<PlanItemResponse> items
) {
}
