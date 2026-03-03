package com.playback.devready.dto;

import jakarta.validation.constraints.NotNull;

public record SelectRoleRequest(@NotNull Long roleId) {
}
