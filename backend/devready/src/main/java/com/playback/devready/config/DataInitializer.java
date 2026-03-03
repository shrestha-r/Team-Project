package com.playback.devready.config;

import com.playback.devready.model.Role;
import com.playback.devready.model.RoleSkill;
import com.playback.devready.model.Skill;
import com.playback.devready.repository.RoleRepository;
import com.playback.devready.repository.RoleSkillRepository;
import com.playback.devready.repository.SkillRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Configuration
public class DataInitializer {
    @Bean
    public CommandLineRunner seedData(RoleRepository roleRepository,
                                      SkillRepository skillRepository,
                                      RoleSkillRepository roleSkillRepository) {
        return args -> {
            Map<String, Skill> skills = seedSkills(skillRepository);
            Map<String, Role> roles = seedRoles(roleRepository);
            if (roleSkillRepository.count() == 0) {
                seedRoleSkills(roleSkillRepository, roles, skills);
            }
        };
    }

    private Map<String, Skill> seedSkills(SkillRepository skillRepository) {
        List<SkillSeed> seeds = List.of(
                new SkillSeed("Java", "Programming", 5),
                new SkillSeed("SQL", "Database", 4),
                new SkillSeed("Git", "Version Control", 3),
                new SkillSeed("REST APIs", "Backend", 4),
                new SkillSeed("DSA", "Problem Solving", 5),
                new SkillSeed("React", "Frontend", 4),
                new SkillSeed("Python", "Programming", 5)
        );

        Map<String, Skill> result = new HashMap<>();
        for (SkillSeed seed : seeds) {
            Skill skill = skillRepository.findByName(seed.name()).orElseGet(() -> {
                Skill created = new Skill();
                created.setName(seed.name());
                created.setCategory(seed.category());
                created.setDefaultImportance(seed.defaultImportance());
                return skillRepository.save(created);
            });
            result.put(seed.name(), skill);
        }
        return result;
    }

    private Map<String, Role> seedRoles(RoleRepository roleRepository) {
        List<RoleSeed> seeds = List.of(
                new RoleSeed("Backend Developer", "Builds scalable backend systems and APIs"),
                new RoleSeed("Frontend Developer", "Builds user-facing web interfaces"),
                new RoleSeed("Data Scientist", "Builds data analysis and ML workflows"),
                new RoleSeed("General SWE", "Balanced software engineering track")
        );

        Map<String, Role> result = new HashMap<>();
        for (RoleSeed seed : seeds) {
            Role role = roleRepository.findByName(seed.name()).orElseGet(() -> {
                Role created = new Role();
                created.setName(seed.name());
                created.setDescription(seed.description());
                return roleRepository.save(created);
            });
            result.put(seed.name(), role);
        }
        return result;
    }

    private void seedRoleSkills(RoleSkillRepository repo, Map<String, Role> roles, Map<String, Skill> skills) {
        addRoleSkill(repo, roles.get("Backend Developer"), skills.get("Java"), 5);
        addRoleSkill(repo, roles.get("Backend Developer"), skills.get("SQL"), 4);
        addRoleSkill(repo, roles.get("Backend Developer"), skills.get("Git"), 3);
        addRoleSkill(repo, roles.get("Backend Developer"), skills.get("REST APIs"), 4);
        addRoleSkill(repo, roles.get("Backend Developer"), skills.get("DSA"), 4);

        addRoleSkill(repo, roles.get("Frontend Developer"), skills.get("React"), 5);
        addRoleSkill(repo, roles.get("Frontend Developer"), skills.get("Git"), 4);
        addRoleSkill(repo, roles.get("Frontend Developer"), skills.get("REST APIs"), 3);
        addRoleSkill(repo, roles.get("Frontend Developer"), skills.get("Java"), 2);

        addRoleSkill(repo, roles.get("Data Scientist"), skills.get("Python"), 5);
        addRoleSkill(repo, roles.get("Data Scientist"), skills.get("SQL"), 4);
        addRoleSkill(repo, roles.get("Data Scientist"), skills.get("DSA"), 4);
        addRoleSkill(repo, roles.get("Data Scientist"), skills.get("Git"), 2);

        addRoleSkill(repo, roles.get("General SWE"), skills.get("Java"), 4);
        addRoleSkill(repo, roles.get("General SWE"), skills.get("SQL"), 3);
        addRoleSkill(repo, roles.get("General SWE"), skills.get("Git"), 3);
        addRoleSkill(repo, roles.get("General SWE"), skills.get("DSA"), 5);
        addRoleSkill(repo, roles.get("General SWE"), skills.get("REST APIs"), 4);
        addRoleSkill(repo, roles.get("General SWE"), skills.get("React"), 2);
    }

    private void addRoleSkill(RoleSkillRepository repo, Role role, Skill skill, int weight) {
        RoleSkill rs = new RoleSkill();
        rs.setRole(role);
        rs.setSkill(skill);
        rs.setImportanceWeight(weight);
        repo.save(rs);
    }

    private record SkillSeed(String name, String category, int defaultImportance) {
    }

    private record RoleSeed(String name, String description) {
    }
}
