# Plan Técnico — 001 Autenticación

> Traduce `spec.md` en decisiones técnicas concretas. Si una decisión cambia, se
> actualiza este plan antes que el código (Constitución I y II).

## Stack

| Capa | Tecnología | Versión |
|------|------------|---------|
| Lenguaje | Java | 21 |
| Framework | Spring Boot (Web, Security, Validation, Data JPA) | 3.5.x |
| ORM | Hibernate (vía Spring Data JPA) | gestionado por Boot |
| Base de datos | MySQL 8 | 8.x (local: servicio MySQL80) |
| JWT | jjwt (api/impl/jackson) | 0.12.6 |
| Hash de contraseñas | BCrypt (`BCryptPasswordEncoder`) | strength 12 |
| Tests backend | JUnit 5 + Spring Test + TestRestTemplate | gestionado por Boot |
| BD en tests | H2 en memoria (perfil de test) | gestionado por Boot |
| Frontend | React + Vite | React 18 / Vite 5 |

## Decisiones de arquitectura

| ID | Decisión | Razón |
|----|----------|-------|
| AD-1 | Monorepo `backend/` + `frontend/` + `specs/` | Un solo lugar para especificación, API y UI. |
| AD-2 | Spring Boot 3.5 + Java 21 | LTS moderno; seguridad y validación integradas. |
| AD-3 | MySQL con credenciales por variables de entorno; H2 en memoria solo para tests | Los tests corren sin depender de un servidor MySQL; producción usa MySQL. |
| AD-4 | BCrypt strength 12 | Estándar del sector para hash de contraseñas; coste ajustado. |
| AD-5 | JWT HS256 con `app.jwt.secret` / `expiration-ms` configurables | Stateless y simple para un proyecto base; el secret se inyecta por entorno. |
| AD-6 | SPA React con proxy de dev `/api → :8080` | Evita CORS en desarrollo y mantiene la API única. |
| AD-7 | Manejo de errores centralizado (`@RestControllerAdvice`) | Cumple FR-9 con un único formato de error. |

## Estructura de paquetes (backend)

```
com.ejelaboral
├── EjelaboralApplication.java
├── auth/        → AuthController, AuthService, dto/
├── user/        → User (entidad JPA), Role, UserRepository
├── security/    → JwtService, JwtAuthFilter, AppUserDetailsService
├── config/      → SecurityConfig (filtro cadena, CORS, PasswordEncoder)
└── common/      → ApiError, GlobalExceptionHandler, EmailAlreadyExistsException
```

## Flujo de autenticación

```
Registro: UI → POST /register → validar → ¿email existe? → 409
                              └→ BCrypt(password) → save(User) → JWT → 201 {token, user}
Login:    UI → POST /login    → AuthenticationManager (UserDetailsService + BCrypt)
                              → JWT → 200 {token, user}
Perfil:   UI → GET /me (Bearer) → JwtAuthFilter valida firma/expiración
                              → carga UserDetails por email → SecurityContext → 200
```

## Estrategia de pruebas

| Nivel | Alcance | Herramienta |
|-------|---------|-------------|
| Integración | Flujos completos HTTP de US1/US2/US3 contra contexto real (H2) | `AuthFlowIntegrationTest` con `TestRestTemplate` |
| Humo | El contexto arranca con todos los beans | `EjelaboralApplicationTests` |
| Manual/E2E | UI completa en navegador | `npm run dev` + backend local |

## Configuración por entorno (variables)

| Variable | Default | Descripción |
|----------|---------|-------------|
| `DB_HOST` / `DB_PORT` / `DB_NAME` | `localhost` / `3306` / `ejelaboral` | Conexión MySQL |
| `DB_USERNAME` / `DB_PASSWORD` | `root` / *(vacía)* | Credenciales MySQL |
| `APP_JWT_SECRET` | valor de desarrollo | Secret HS256 (≥ 32 bytes). En producción SIEMPRE por entorno. |
| `APP_JWT_EXPIRATION_MS` | `3600000` (1 h) | Vida del token |
| `APP_CORS_ORIGINS` | `http://localhost:5173,...` | Orígenes CORS permitidos |

## Riesgos y mitigaciones

- **MySQL local no disponible** → `docker-compose.yml` levanta MySQL en el puerto 3307 (`DB_PORT=3307`).
- **Secret de JWT en desarrollo commiteado** → solo es un placeholder; la constitución obliga a rotarlo por `APP_JWT_SECRET` en producción.
