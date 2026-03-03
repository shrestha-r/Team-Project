package com.playback.devready.dto;

public record AuthResponse(String token, Long userId, String email, Integer dailyTimeLimit) {
}
