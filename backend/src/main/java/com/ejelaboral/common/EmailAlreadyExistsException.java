package com.ejelaboral.common;

/** Se lanza al intentar registrar un email ya existente (US1-AC2 → HTTP 409). */
public class EmailAlreadyExistsException extends RuntimeException {

    public EmailAlreadyExistsException(String email) {
        super("El email ya está registrado: " + email);
    }
}
