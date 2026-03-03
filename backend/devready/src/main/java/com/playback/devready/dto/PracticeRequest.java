package com.playback.devready.dto;

import jakarta.validation.constraints.*;

import java.time.LocalDate;

public record PracticeRequest(
        @NotNull Long userSkillId,
        @NotNull @Min(1) @Max(300) Integer minutes,
        @Size(max = 1000) String notes,
        @Min(1) @Max(10) Integer confidence,
        LocalDate practiceDate
) {
}
