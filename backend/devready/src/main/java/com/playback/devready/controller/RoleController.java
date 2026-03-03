package com.playback.devready.controller;

import com.playback.devready.dto.MessageResponse;
import com.playback.devready.dto.RoleResponse;
import com.playback.devready.dto.SelectRoleRequest;
import com.playback.devready.security.CurrentUserPrincipal;
import com.playback.devready.service.RoleService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@RestController
@RequestMapping("/api")
public class RoleController {
    private final RoleService roleService;

    public RoleController(RoleService roleService) {
        this.roleService = roleService;
    }

    @GetMapping("/roles")
    public ResponseEntity<List<RoleResponse>> getRoles() {
        return ResponseEntity.ok(roleService.getAllRoles());
    }

    @PostMapping("/users/select-role")
    public ResponseEntity<MessageResponse> selectRole(@AuthenticationPrincipal CurrentUserPrincipal principal,
                                                      @Valid @RequestBody SelectRoleRequest request) {
        if (principal == null) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Authentication required");
        }
        return ResponseEntity.ok(roleService.selectRole(principal.getId(), request.roleId()));
    }
}
