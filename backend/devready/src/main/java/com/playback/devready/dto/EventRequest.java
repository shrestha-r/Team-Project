package com.playback.devready.dto;

import com.playback.devready.model.EventType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.time.LocalDate;

public record EventRequest(
        @NotBlank @Size(max = 200) String title,
        @NotNull LocalDate eventDate,
        @NotNull EventType type
) {
}
