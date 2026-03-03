package com.playback.devready.service;

import com.playback.devready.dto.MessageResponse;
import com.playback.devready.dto.RoleResponse;
import com.playback.devready.model.Role;
import com.playback.devready.model.RoleSkill;
import com.playback.devready.model.User;
import com.playback.devready.model.UserSkill;
import com.playback.devready.repository.RoleRepository;
import com.playback.devready.repository.RoleSkillRepository;
import com.playback.devready.repository.UserRepository;
import com.playback.devready.repository.UserSkillRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDate;
import java.util.Comparator;
import java.util.List;

@Service
public class RoleService {
    private final RoleRepository roleRepository;
    private final RoleSkillRepository roleSkillRepository;
    private final UserRepository userRepository;
    private final UserSkillRepository userSkillRepository;

    public RoleService(RoleRepository roleRepository, RoleSkillRepository roleSkillRepository,
                       UserRepository userRepository, UserSkillRepository userSkillRepository) {
        this.roleRepository = roleRepository;
        this.roleSkillRepository = roleSkillRepository;
        this.userRepository = userRepository;
        this.userSkillRepository = userSkillRepository;
    }

    public List<RoleResponse> getAllRoles() {
        return roleRepository.findAll().stream()
                .sorted(Comparator.comparing(Role::getName))
                .map(role -> new RoleResponse(role.getId(), role.getName(), role.getDescription()))
                .toList();
    }

    @Transactional
    public MessageResponse selectRole(Long userId, Long roleId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"));
        Role role = roleRepository.findById(roleId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Role not found"));

        List<RoleSkill> roleSkills = roleSkillRepository.findByRoleId(role.getId());
        if (roleSkills.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Selected role has no skill mappings");
        }

        int synced = 0;
        for (RoleSkill roleSkill : roleSkills) {
            UserSkill userSkill = userSkillRepository
                    .findByUserIdAndSkillId(userId, roleSkill.getSkill().getId())
                    .orElseGet(UserSkill::new);
            userSkill.setUser(user);
            userSkill.setSkill(roleSkill.getSkill());
            userSkill.setCustomImportance(roleSkill.getImportanceWeight());
            if (userSkill.getConfidence() == null) userSkill.setConfidence(5);
            if (userSkill.getLastPracticed() == null) userSkill.setLastPracticed(LocalDate.now());
            userSkillRepository.save(userSkill);
            synced++;
        }

        return new MessageResponse("Role selected. " + synced + " skills are ready.");
    }
}
