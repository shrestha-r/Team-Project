package com.playback.devready.service;

import com.playback.devready.dto.*;
import com.playback.devready.model.PracticeLog;
import com.playback.devready.model.Skill;
import com.playback.devready.model.UserSkill;
import com.playback.devready.repository.PracticeLogRepository;
import com.playback.devready.repository.SkillRepository;
import com.playback.devready.repository.UserSkillRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;
import java.util.Map;

@Service
public class SkillService {
    private final SkillRepository skillRepository;
    private final UserSkillRepository userSkillRepository;
    private final PracticeLogRepository practiceLogRepository;
    private final UrgencyEngineService urgencyEngineService;
    private static final double DEFAULT_DECAY_RATE = 0.07;
    private static final double REMINDER_THRESHOLD = 35.0;
    private static final double CEMETERY_THRESHOLD = 15.0;
    private static final double RELEARN_DIVISOR = 14.0;

    private static final Map<String, Double> CATEGORY_DECAY_RATES = Map.ofEntries(
            Map.entry("language", 0.12),
            Map.entry("spoken language", 0.12),
            Map.entry("programming", 0.08),
            Map.entry("backend", 0.08),
            Map.entry("frontend", 0.08),
            Map.entry("database", 0.08),
            Map.entry("technical", 0.08),
            Map.entry("problem solving", 0.06),
            Map.entry("version control", 0.06),
            Map.entry("instrument", 0.045),
            Map.entry("physical", 0.05),
            Map.entry("theoretical", 0.06)
    );

    public SkillService(SkillRepository skillRepository, UserSkillRepository userSkillRepository,
                        PracticeLogRepository practiceLogRepository, UrgencyEngineService urgencyEngineService) {
        this.skillRepository = skillRepository;
        this.userSkillRepository = userSkillRepository;
        this.practiceLogRepository = practiceLogRepository;
        this.urgencyEngineService = urgencyEngineService;
    }

    public List<SkillResponse> getAllSkills() {
        return skillRepository.findAll().stream()
                .sorted(Comparator.comparing(Skill::getName))
                .map(skill -> new SkillResponse(skill.getId(), skill.getName(), skill.getCategory(), skill.getDefaultImportance()))
                .toList();
    }

    public List<UserSkillResponse> getUserSkills(Long userId) {
        LocalDate today = LocalDate.now();

        return userSkillRepository.findByUserId(userId).stream()
                .map(userSkill -> {
                    SkillHealth health = buildHealth(userSkill, today);
                    return new UserSkillResponse(
                            userSkill.getId(),
                            userSkill.getSkill().getId(),
                            userSkill.getSkill().getName(),
                            userSkill.getSkill().getCategory(),
                            userSkill.getConfidence(),
                            userSkill.getLastPracticed(),
                            urgencyEngineService.resolveImportance(userSkill),
                            urgencyEngineService.calculateUrgency(userSkill, today),
                            health.healthScore(),
                            health.decayRate(),
                            health.daysSince(),
                            health.daysToReminder(),
                            health.daysToCemetery(),
                            health.relearnWeeks()
                    );
                })
                .sorted((a, b) -> Double.compare(b.urgency(), a.urgency()))
                .toList();
    }

    @Transactional
    public MessageResponse logPractice(Long userId, PracticeRequest request) {
        UserSkill userSkill = userSkillRepository.findById(request.userSkillId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User skill not found"));

        if (!userSkill.getUser().getId().equals(userId)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "You cannot log practice for another user");
        }

        PracticeLog log = new PracticeLog();
        log.setUserSkill(userSkill);
        log.setMinutes(request.minutes());
        log.setNotes(request.notes());
        log.setPracticeDate(request.practiceDate() == null ? LocalDate.now() : request.practiceDate());
        practiceLogRepository.save(log);

        userSkill.setLastPracticed(log.getPracticeDate());
        if (request.confidence() != null) userSkill.setConfidence(request.confidence());
        userSkillRepository.save(userSkill);

        return new MessageResponse("Practice logged successfully");
    }

    private SkillHealth buildHealth(UserSkill userSkill, LocalDate today) {
        LocalDate practiced = userSkill.getLastPracticed() != null ? userSkill.getLastPracticed() : today;
        long daysSince = Math.max(0, ChronoUnit.DAYS.between(practiced, today));
        double decayRate = resolveDecayRate(userSkill.getSkill().getCategory());
        double rawHealth = 100.0 * Math.exp(-decayRate * daysSince);
        double healthScore = Math.max(0.0, Math.min(100.0, rawHealth));
        double reminderLifetime = daysToThreshold(decayRate, REMINDER_THRESHOLD);
        double cemeteryLifetime = daysToThreshold(decayRate, CEMETERY_THRESHOLD);
        double daysToReminder = Math.max(0.0, reminderLifetime - daysSince);
        double daysToCemetery = Math.max(0.0, cemeteryLifetime - daysSince);
        int relearnWeeks = (int) Math.max(1, Math.ceil((100.0 - healthScore) / RELEARN_DIVISOR));
        return new SkillHealth(healthScore, decayRate, (int) daysSince, daysToReminder, daysToCemetery, relearnWeeks);
    }

    private double daysToThreshold(double decayRate, double thresholdPercent) {
        if (decayRate <= 0 || thresholdPercent <= 0 || thresholdPercent >= 100) {
            return 0.0;
        }
        return -Math.log(thresholdPercent / 100.0) / decayRate;
    }

    private double resolveDecayRate(String category) {
        if (category == null || category.isBlank()) {
            return DEFAULT_DECAY_RATE;
        }
        return CATEGORY_DECAY_RATES.getOrDefault(category.trim().toLowerCase(Locale.ROOT), DEFAULT_DECAY_RATE);
    }

    private record SkillHealth(
            double healthScore,
            double decayRate,
            int daysSince,
            double daysToReminder,
            double daysToCemetery,
            int relearnWeeks
    ) {
    }
}
