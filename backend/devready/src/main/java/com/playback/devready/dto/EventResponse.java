package com.playback.devready.dto;

import com.playback.devready.model.EventType;

import java.time.LocalDate;

public record EventResponse(Long id, String title, LocalDate eventDate, EventType type) {
}
