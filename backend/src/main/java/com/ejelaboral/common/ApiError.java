package com.ejelaboral.common;

import java.time.Instant;
import java.util.Map;

/**
 * Cuerpo único de errores HTTP (FR-9):
 * { status, error, message, fieldErrors, timestamp }.
 */
public record ApiError(
        int status,
        String error,
        String message,
        Map<String, String> fieldErrors,
        Instant timestamp
) {
    public static ApiError of(int status, String error, String message) {
        return new ApiError(status, error, message, null, Instant.now());
    }
}
