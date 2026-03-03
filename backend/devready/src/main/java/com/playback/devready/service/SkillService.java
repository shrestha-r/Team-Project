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
import java.util.Comparator;
import java.util.List;

@Service
public class SkillService {
    private final SkillRepository skillRepository;
    private final UserSkillRepository userSkillRepository;
    private final PracticeLogRepository practiceLogRepository;
    private final UrgencyEngineService urgencyEngineService;

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
                .map(userSkill -> new UserSkillResponse(
                        userSkill.getId(),
                        userSkill.getSkill().getId(),
                        userSkill.getSkill().getName(),
                        userSkill.getSkill().getCategory(),
                        userSkill.getConfidence(),
                        userSkill.getLastPracticed(),
                        urgencyEngineService.resolveImportance(userSkill),
                        urgencyEngineService.calculateUrgency(userSkill, today)
                ))
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
}
