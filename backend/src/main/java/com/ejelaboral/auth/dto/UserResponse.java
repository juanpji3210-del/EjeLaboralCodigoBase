package com.ejelaboral.auth.dto;

import com.ejelaboral.user.User;

import java.time.Instant;

/**
 * Perfil público del usuario. NUNCA incluye passwordHash.
 * Se usa en respuestas de auth (FR-7) y en GET /api/auth/me (FR-8).
 */
public record UserResponse(
        Long id,
        String email,
        String fullName,
        String role,
        Instant createdAt
) {
    public static UserResponse from(User user) {
        return new UserResponse(
                user.getId(),
                user.getEmail(),
                user.getFullName(),
                user.getRole().name(),
                user.getCreatedAt());
    }
}
