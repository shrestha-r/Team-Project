package com.playback.devready.controller;

import com.playback.devready.dto.*;
import com.playback.devready.security.CurrentUserPrincipal;
import com.playback.devready.service.SkillService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@RestController
@RequestMapping("/api")
public class SkillController {
    private final SkillService skillService;

    public SkillController(SkillService skillService) {
        this.skillService = skillService;
    }

    @GetMapping("/skills")
    public ResponseEntity<List<SkillResponse>> getSkills() {
        return ResponseEntity.ok(skillService.getAllSkills());
    }

    @GetMapping("/userskills")
    public ResponseEntity<List<UserSkillResponse>> getUserSkills(@AuthenticationPrincipal CurrentUserPrincipal principal) {
        if (principal == null) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Authentication required");
        }
        return ResponseEntity.ok(skillService.getUserSkills(principal.getId()));
    }

    @PostMapping("/practice")
    public ResponseEntity<MessageResponse> logPractice(@AuthenticationPrincipal CurrentUserPrincipal principal,
                                                       @Valid @RequestBody PracticeRequest request) {
        if (principal == null) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Authentication required");
        }
        return ResponseEntity.ok(skillService.logPractice(principal.getId(), request));
    }
}
