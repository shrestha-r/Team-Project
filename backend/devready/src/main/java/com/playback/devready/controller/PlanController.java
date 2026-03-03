package com.playback.devready.controller;

import com.playback.devready.dto.TodayPlanResponse;
import com.playback.devready.security.CurrentUserPrincipal;
import com.playback.devready.service.DailyPlanService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

@RestController
@RequestMapping("/api/plan")
public class PlanController {
    private final DailyPlanService dailyPlanService;

    public PlanController(DailyPlanService dailyPlanService) {
        this.dailyPlanService = dailyPlanService;
    }

    @GetMapping("/today")
    public ResponseEntity<TodayPlanResponse> getTodayPlan(@AuthenticationPrincipal CurrentUserPrincipal principal) {
        if (principal == null) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Authentication required");
        }
        return ResponseEntity.ok(dailyPlanService.generateTodayPlan(principal.getId()));
    }
}
