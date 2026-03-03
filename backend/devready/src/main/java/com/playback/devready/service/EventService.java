package com.playback.devready.service;

import com.playback.devready.dto.EventRequest;
import com.playback.devready.dto.EventResponse;
import com.playback.devready.model.Event;
import com.playback.devready.model.User;
import com.playback.devready.repository.EventRepository;
import com.playback.devready.repository.UserRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Service
public class EventService {
    private final EventRepository eventRepository;
    private final UserRepository userRepository;

    public EventService(EventRepository eventRepository, UserRepository userRepository) {
        this.eventRepository = eventRepository;
        this.userRepository = userRepository;
    }

    @Transactional
    public EventResponse createEvent(Long userId, EventRequest request) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"));

        Event event = new Event();
        event.setUser(user);
        event.setTitle(request.title());
        event.setEventDate(request.eventDate());
        event.setType(request.type());
        Event saved = eventRepository.save(event);

        return new EventResponse(saved.getId(), saved.getTitle(), saved.getEventDate(), saved.getType());
    }

    public List<EventResponse> getEvents(Long userId) {
        return eventRepository.findByUserIdOrderByEventDateAsc(userId).stream()
                .map(e -> new EventResponse(e.getId(), e.getTitle(), e.getEventDate(), e.getType()))
                .toList();
    }
}
