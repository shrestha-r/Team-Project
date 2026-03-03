package com.playback.devready.service;

import com.playback.devready.dto.PlanItemResponse;
import com.playback.devready.dto.TodayPlanResponse;
import com.playback.devready.model.User;
import com.playback.devready.model.UserSkill;
import com.playback.devready.repository.UserRepository;
import com.playback.devready.repository.UserSkillRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

@Service
public class DailyPlanService {
    private final UserRepository userRepository;
    private final UserSkillRepository userSkillRepository;
    private final UrgencyEngineService urgencyEngineService;

    public DailyPlanService(UserRepository userRepository, UserSkillRepository userSkillRepository,
                            UrgencyEngineService urgencyEngineService) {
        this.userRepository = userRepository;
        this.userSkillRepository = userSkillRepository;
        this.urgencyEngineService = urgencyEngineService;
    }

    public TodayPlanResponse generateTodayPlan(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"));

        int dailyLimit = user.getDailyTimeLimit() == null ? 60 : user.getDailyTimeLimit();
        LocalDate today = LocalDate.now();

        List<UserSkill> userSkills = userSkillRepository.findByUserId(userId);
        if (userSkills.isEmpty()) {
            return new TodayPlanResponse(today, dailyLimit, 0, 0, List.of());
        }

        List<RankedSkill> ranked = userSkills.stream()
                .map(skill -> new RankedSkill(skill, urgencyEngineService.calculateUrgency(skill, today)))
                .sorted(Comparator.comparingDouble(RankedSkill::urgency).reversed())
                .toList();

        int remaining = dailyLimit;
        List<PlanItemResponse> items = new ArrayList<>();

        for (RankedSkill rankedSkill : ranked) {
            if (remaining <= 0) break;

            int allocated = suggestMinutes(rankedSkill.urgency(), remaining);
            if (allocated <= 0) continue;

            items.add(new PlanItemResponse(
                    rankedSkill.userSkill().getId(),
                    rankedSkill.userSkill().getSkill().getId(),
                    rankedSkill.userSkill().getSkill().getName(),
                    allocated,
                    rankedSkill.urgency()
            ));
            remaining -= allocated;
        }

        int readinessScore = computeReadinessScore(userSkills, today);
        return new TodayPlanResponse(today, dailyLimit, dailyLimit - remaining, readinessScore, items);
    }

    private int suggestMinutes(double urgency, int remaining) {
        int suggested = (int) Math.round(urgency * 3.0);
        suggested = Math.max(suggested, 5);
        suggested = Math.min(suggested, 30);
        return Math.min(suggested, remaining);
    }

    private int computeReadinessScore(List<UserSkill> userSkills, LocalDate today) {
        double avgConfidence = userSkills.stream().mapToInt(UserSkill::getConfidence).average().orElse(0);
        double avgDaysSince = userSkills.stream()
                .mapToLong(skill -> Math.max(0, ChronoUnit.DAYS.between(skill.getLastPracticed(), today)))
                .average()
                .orElse(0);

        double confidenceScore = (avgConfidence / 10.0) * 70.0;
        double recencyScore = (Math.max(0, 30.0 - avgDaysSince) / 30.0) * 30.0;

        int score = (int) Math.round(confidenceScore + recencyScore);
        return Math.max(0, Math.min(100, score));
    }

    private record RankedSkill(UserSkill userSkill, double urgency) {
    }
}
