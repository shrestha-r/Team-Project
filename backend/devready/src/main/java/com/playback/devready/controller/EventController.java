package com.playback.devready.controller;

import com.playback.devready.dto.EventRequest;
import com.playback.devready.dto.EventResponse;
import com.playback.devready.security.CurrentUserPrincipal;
import com.playback.devready.service.EventService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@RestController
@RequestMapping("/api/events")
public class EventController {
    private final EventService eventService;

    public EventController(EventService eventService) {
        this.eventService = eventService;
    }

    @PostMapping
    public ResponseEntity<EventResponse> createEvent(@AuthenticationPrincipal CurrentUserPrincipal principal,
                                                     @Valid @RequestBody EventRequest request) {
        if (principal == null) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Authentication required");
        }
        return ResponseEntity.status(HttpStatus.CREATED).body(eventService.createEvent(principal.getId(), request));
    }

    @GetMapping
    public ResponseEntity<List<EventResponse>> getEvents(@AuthenticationPrincipal CurrentUserPrincipal principal) {
        if (principal == null) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Authentication required");
        }
        return ResponseEntity.ok(eventService.getEvents(principal.getId()));
    }
}
