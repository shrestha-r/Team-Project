package com.playback.devready.repository;

import com.playback.devready.model.PracticeLog;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PracticeLogRepository extends JpaRepository<PracticeLog, Long> {
    List<PracticeLog> findByUserSkillId(Long userSkillId);
}
