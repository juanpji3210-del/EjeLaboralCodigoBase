package com.ejelaboral.auth.dto;

/** Respuesta de registro/login — formato { accessToken, tokenType, user } (FR-7). */
public record AuthResponse(
        String accessToken,
        String tokenType,
        UserResponse user
) {
}
