# Tareas — 001 Autenticación

> Desglose de implementación con trazabilidad a la spec. Cada tarea referencia su
> historia de usuario (US) y requisitos (FR). Constitución V: tarea terminada = código + prueba.

## Fase 1 — Fundaciones

- [x] T001 Crear estructura de repo SDD (`specs/`, `.specify/`, `backend/`, `frontend/`)
- [x] T002 `pom.xml` Spring Boot 3.5 + Java 21 con dependencias web/security/jpa/validation/jjwt/mysql/h2 (AD-2, AD-5)
- [x] T003 Configuración `application.yml` con variables de entorno y perfil de test H2 (AD-3)

## Fase 2 — Dominio y ORM (FR-14)

- [x] T010 Entidad `User` + enum `Role` (mapping según `data-model.md`)
- [x] T011 `UserRepository` con `findByEmailIgnoreCase` y `existsByEmailIgnoreCase` (FR-4)

## Fase 3 — Seguridad JWT (FR-3, FR-6, FR-11)

- [x] T020 `SecurityConfig`: stateless, CSRF off, CORS configurable, entry point 401 JSON (FR-9, FR-10, FR-11)
- [x] T021 `JwtService`: generación/validación HS256 con expiración configurable (FR-6)
- [x] T022 `JwtAuthFilter`: extraer Bearer, validar, poblar `SecurityContext`
- [x] T023 `AppUserDetailsService`: carga por email con roles y estado `enabled`
- [x] T024 `PasswordEncoder` BCrypt strength 12 (FR-3)

## Fase 4 — API de autenticación (FR-1, FR-2, FR-5, FR-7, FR-8, FR-9)

- [x] T030 DTOs con validación declarativa (`RegisterRequest`, `LoginRequest`, `AuthResponse`, `UserResponse`)
- [x] T031 `AuthService.register`: normalización de email, unicidad, hash BCrypt (FR-2, FR-3, FR-4)
- [x] T032 `AuthService.login` vía `AuthenticationManager` (FR-5)
- [x] T033 `AuthController` (register/login/me) según contrato
- [x] T034 `GlobalExceptionHandler` + `ApiError` (FR-9)

## Fase 5 — Frontend React (FR-12, FR-13)

- [x] T040 Scaffold Vite + proxy `/api` al backend (AD-6)
- [x] T041 Pestañas login/registro con validación cliente y estados de carga/error
- [x] T042 Persistencia del token en `localStorage`, recuperación de sesión al recargar, logout (FR-13, US3-AC3)

## Fase 6 — Pruebas (Constitución V)

- [x] T050 Test de humo: contexto arranca con H2
- [x] T051 `AuthFlowIntegrationTest`: US1-AC1/AC2, US2-AC1/AC2, US3-AC1/AC2
- [x] T052 Verificación manual E2E: UI ↔ backend (documentada en README)

## Fase 7 — Documentación

- [x] T060 README con metodología SDD, arranque backend/frontend y variables de entorno

## Backlog (fuera de esta spec — requiere nuevas specs)

- [ ] T100 Refresh tokens y logout con lista de revocación → *spec 002*
- [ ] T101 Recuperación de contraseña por email → *spec 003*
- [ ] T102 Verificación de email al registrarse → *spec 003*
- [ ] T103 Roles y autorización granular (`ADMIN`, `@PreAuthorize`) → *spec 004*
- [ ] T104 Rate limiting en endpoints públicos → *spec 004*
- [ ] T105 CI (GitHub Actions): build + tests en cada PR
- [ ] T106 E2E automatizado de la UI (Playwright)
