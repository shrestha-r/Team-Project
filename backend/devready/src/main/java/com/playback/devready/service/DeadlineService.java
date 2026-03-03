package com.playback.devready.service;

import com.playback.devready.model.Event;
import com.playback.devready.model.EventType;
import com.playback.devready.model.Skill;
import com.playback.devready.model.User;
import com.playback.devready.repository.EventRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Locale;

@Service
public class DeadlineService {
    private final EventRepository eventRepository;

    public DeadlineService(EventRepository eventRepository) {
        this.eventRepository = eventRepository;
    }

    public double getFactor(User user, Skill skill, LocalDate today) {
        List<Event> upcoming = eventRepository.findByUserIdAndEventDateGreaterThanEqualOrderByEventDateAsc(user.getId(), today);
        double factor = 1.0;

        for (Event event : upcoming) {
            long daysUntil = ChronoUnit.DAYS.between(today, event.getEventDate());
            if (daysUntil > 45) continue;

            boolean relevant = isRelevant(skill, event.getType());
            factor = Math.max(factor, factorForEvent(event.getType(), daysUntil, relevant));
        }
        return factor;
    }

    private double factorForEvent(EventType type, long daysUntil, boolean relevant) {
        double proximity = Math.max(0, (45.0 - daysUntil) / 45.0);
        return switch (type) {
            case INTERVIEW -> relevant ? 1.0 + (1.0 * proximity) : 1.0;
            case EXAM -> 1.0 + (0.7 * proximity);
            case PERSONAL -> 1.0 + (0.3 * proximity);
        };
    }

    private boolean isRelevant(Skill skill, EventType type) {
        if (type != EventType.INTERVIEW) return true;

        String name = skill.getName().toLowerCase(Locale.ROOT);
        return name.contains("dsa") || name.contains("algorithm") || name.contains("sql") ||
                name.contains("java") || name.contains("git") || name.contains("rest") ||
                name.contains("api") || name.contains("react") || name.contains("python");
    }
}
