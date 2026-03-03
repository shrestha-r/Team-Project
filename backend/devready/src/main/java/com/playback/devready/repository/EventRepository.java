package com.playback.devready.repository;

import com.playback.devready.model.Event;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;

public interface EventRepository extends JpaRepository<Event, Long> {
    List<Event> findByUserIdOrderByEventDateAsc(Long userId);
    List<Event> findByUserIdAndEventDateGreaterThanEqualOrderByEventDateAsc(Long userId, LocalDate date);
}
