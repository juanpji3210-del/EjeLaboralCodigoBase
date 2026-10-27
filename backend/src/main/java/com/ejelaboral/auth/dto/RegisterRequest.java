package com.ejelaboral.auth.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

/**
 * Solicitud de registro — contrato POST /api/auth/register (FR-1, FR-2).
 * La validación se declara aquí, en el borde (Constitución IV).
 */
public record RegisterRequest(

        @NotBlank(message = "El email es obligatorio")
        @Email(message = "Debe ser un email válido")
        @Size(max = 180, message = "El email no puede superar 180 caracteres")
        String email,

        @NotBlank(message = "La contraseña es obligatoria")
        @Size(min = 8, max = 100, message = "La contraseña debe tener entre 8 y 100 caracteres")
        String password,

        @NotBlank(message = "El nombre es obligatorio")
        @Size(min = 2, max = 120, message = "El nombre debe tener entre 2 y 120 caracteres")
        String fullName
) {
}
