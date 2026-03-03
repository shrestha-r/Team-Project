package com.playback.devready.dto;

import jakarta.validation.constraints.*;

public record RegisterRequest(
        @NotBlank @Email String email,
        @NotBlank @Size(min = 8, max = 120) String password,
        @Min(15) @Max(480) Integer dailyTimeLimit
) {
}
