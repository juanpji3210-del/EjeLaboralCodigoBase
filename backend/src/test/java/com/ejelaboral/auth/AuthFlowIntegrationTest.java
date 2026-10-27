package com.ejelaboral.auth;

import com.ejelaboral.auth.dto.AuthResponse;
import com.ejelaboral.auth.dto.LoginRequest;
import com.ejelaboral.auth.dto.RegisterRequest;
import com.ejelaboral.auth.dto.UserResponse;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Suite de integración HTTP (T051) — verifica los criterios de aceptación
 * de specs/001-autenticacion/spec.md contra el contexto completo (H2 en memoria).
 *
 * US1: registro · US2: login · US3: sesión protegida (/me).
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class AuthFlowIntegrationTest {

    @Autowired
    private TestRestTemplate rest;

    private final String email = "test." + UUID.randomUUID() + "@example.com";
    private final String password = "Secreta123";

    @Test
    void flujoCompletoRegistroLoginYPerfil() {
        // ── US1-AC1: registro válido → 201 + token + usuario ────────────────
        ResponseEntity<AuthResponse> register = rest.postForEntity(
                "/api/auth/register",
                new RegisterRequest(email, password, "Test User"),
                AuthResponse.class);

        assertEquals(201, register.getStatusCode().value());
        AuthResponse registerBody = register.getBody();
        assertNotNull(registerBody);
        assertNotNull(registerBody.accessToken());
        assertEquals("Bearer", registerBody.tokenType());
        assertEquals(email, registerBody.user().email());
        assertEquals("USER", registerBody.user().role());
        assertNotEquals(password, registerBody.user().email()); // sanity: nunca se expone el hash

        // ── US1-AC2: email duplicado → 409 ──────────────────────────────────
        ResponseEntity<String> duplicate = rest.postForEntity(
                "/api/auth/register",
                new RegisterRequest(email, password, "Otro Nombre"),
                String.class);
        assertEquals(409, duplicate.getStatusCode().value());

        // ── US1-AC3: validación → 400 con fieldErrors ───────────────────────
        ResponseEntity<String> invalid = rest.postForEntity(
                "/api/auth/register",
                new RegisterRequest("no-es-un-email", "corta", ""),
                String.class);
        assertEquals(400, invalid.getStatusCode().value());
        assertTrue(invalid.getBody().contains("fieldErrors"));

        // ── US2-AC1: login correcto → 200 + token ───────────────────────────
        ResponseEntity<AuthResponse> login = rest.postForEntity(
                "/api/auth/login",
                new LoginRequest(email, password),
                AuthResponse.class);
        assertEquals(200, login.getStatusCode().value());
        assertNotNull(login.getBody());
        assertNotNull(login.getBody().accessToken());

        // ── US2-AC2: contraseña incorrecta → 401 genérico ───────────────────
        ResponseEntity<String> badLogin = rest.postForEntity(
                "/api/auth/login",
                new LoginRequest(email, "ContrasenaIncorreta1"),
                String.class);
        assertEquals(401, badLogin.getStatusCode().value());
        assertTrue(badLogin.getBody().contains("Email o contraseña incorrectos"));

        // ── US2-AC3: email case-insensitive en login ────────────────────────
        ResponseEntity<AuthResponse> loginUpper = rest.postForEntity(
                "/api/auth/login",
                new LoginRequest(email.toUpperCase(), password),
                AuthResponse.class);
        assertEquals(200, loginUpper.getStatusCode().value());

        // ── US3-AC1: /me con token → 200 + perfil ───────────────────────────
        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(login.getBody().accessToken());
        ResponseEntity<UserResponse> me = rest.exchange(
                "/api/auth/me", HttpMethod.GET, new HttpEntity<>(headers), UserResponse.class);
        assertEquals(200, me.getStatusCode().value());
        assertEquals(email, me.getBody().email());
        assertEquals("Test User", me.getBody().fullName());

        // ── US3-AC2: /me sin token → 401 ────────────────────────────────────
        ResponseEntity<String> noAuth = rest.getForEntity("/api/auth/me", String.class);
        assertEquals(401, noAuth.getStatusCode().value());

        // ── US3-AC2: /me con token manipulado → 401 ─────────────────────────
        HttpHeaders tampered = new HttpHeaders();
        tampered.setBearerAuth(login.getBody().accessToken() + "xyz");
        ResponseEntity<String> tamperedMe = rest.exchange(
                "/api/auth/me", HttpMethod.GET, new HttpEntity<>(tampered), String.class);
        assertEquals(401, tamperedMe.getStatusCode().value());
    }
}
