# Especificación 001 — Autenticación (Registro / Login)

- **Estado:** Implementada
- **Fecha:** 2026-10-27
- **Prioridad:** Alta (fundación del producto)

## Resumen

El sistema debe permitir que una persona cree una cuenta (registro), inicie sesión con
sus credenciales y mantenga una sesión stateless basada en JWT. Incluye una interfaz
web de login y registro, un backend Spring Boot con persistencia en MySQL vía JPA,
contraseñas hasheadas con BCrypt y autenticación Bearer JWT.

## Historias de Usuario

### US1 — Registro de usuario
**Como** visitante, **quiero** crear una cuenta con mi email, nombre y contraseña,
**para** acceder a la aplicación.

**Criterios de aceptación**
- AC1: Un registro válido (email, contraseña ≥ 8 caracteres, nombre) responde `201` con un JWT y los datos del usuario creado.
- AC2: Intentar registrar un email ya existente responde `409` sin exponer datos del usuario existente.
- AC3: Entrada inválida (email mal formado, contraseña < 8, nombre vacío) responde `400` indicando los errores por campo.
- AC4: La contraseña se almacena hasheada (BCrypt); jamás en texto plano ni reversible.

### US2 — Inicio de sesión
**Como** usuario registrado, **quiero** iniciar sesión con mi email y contraseña,
**para** obtener un token de acceso.

**Criterios de aceptación**
- AC1: Credenciales correctas responden `200` con `accessToken`, `tokenType` y el perfil del usuario.
- AC2: Email inexistente o contraseña incorrecta responden `401` con un mensaje genérico (no se revela cuál campo falló).
- AC3: El email es insensible a mayúsculas/minúsculas al buscar la cuenta.

### US3 — Sesión protegida (perfil)
**Como** usuario autenticado, **quiero** consultar mi perfil con mi token,
**para** confirmar que mi sesión es válida.

**Criterios de aceptación**
- AC1: `GET /api/auth/me` con `Authorization: Bearer <token válido>` responde `200` con el perfil.
- AC2: Sin token, con token malformado, manipulado o expirado responde `401` con cuerpo JSON de error.
- AC3: La UI guarda el token, recupera la sesión al recargar la página y permite cerrar sesión (elimina el token localmente).

## Requisitos Funcionales

| ID | Requisito |
|----|-----------|
| FR-1 | El sistema expone `POST /api/auth/register` con `email`, `password`, `fullName`. |
| FR-2 | Validación declarativa: email con formato válido y ≤ 180 chars; contraseña entre 8 y 100 chars; nombre entre 2 y 120 chars. |
| FR-3 | Contraseña hasheada con BCrypt (strength 12) antes de persistir. |
| FR-4 | Unicidad de email insensible a mayúsculas; los emails se normalizan a minúsculas. |
| FR-5 | El sistema expone `POST /api/auth/login` validando credenciales con `AuthenticationManager`. |
| FR-6 | JWT firmado HS256, subject = email, con emisión y expiración configurables (default 1 hora). |
| FR-7 | Respuestas de auth: `{ accessToken, tokenType: "Bearer", user }`. |
| FR-8 | El sistema expone `GET /api/auth/me`, protegido, que devuelve el perfil del usuario del token. |
| FR-9 | Todos los errores HTTP usan el cuerpo JSON `{ status, error, message, fieldErrors, timestamp }`. |
| FR-10 | CORS habilitado para los orígenes del frontend (configurable por variable de entorno). |
| FR-11 | Sesiones stateless: sin cookies de sesión, sin estado en servidor; la seguridad se resuelve por filtro JWT. |
| FR-12 | UI web con pestañas "Iniciar sesión" y "Registrarse", validación básica en cliente y estados de carga/error. |
| FR-13 | Tras autenticarse, la UI muestra el perfil del usuario y un botón de cerrar sesión. |
| FR-14 | Persistencia de usuarios en MySQL vía JPA/Hibernate (entidad `User`, tabla `users`). |

## Requisitos No Funcionales

- **Seguridad:** BCrypt strength ≥ 10; JWT HS256 con secret ≥ 32 bytes configurable por `APP_JWT_SECRET`; endpoints autenticados por defecto.
- **Rendimiento:** login/registro < 500 ms en entorno local.
- **Mantenibilidad:** arquitectura por paquetes (auth, user, security, config, common); sin Lombok; constructor injection.

## Casos límite (edge cases)

- Email con mayúsculas/espacios en los bordes → normalizado (trim + lowercase).
- Contraseña exactamente de 8 caracteres → válida.
- Token firmado con otro secret → inválido (`401`).
- Token válido de un usuario eliminado de la BD → `401` (el filtro no autentica).
- Cuerpo JSON malformado → `400`.

## Fuera de alcance (esta spec)

Refresh tokens, recuperación de contraseña, verificación por email, OAuth/social login,
rate limiting, roles granulares más allá de `USER` (ver `tasks.md` — backlog).

## Criterios de éxito

- `mvn test` verde: la suite de integración cubre US1/US2/US3 (registro, duplicado, login, login inválido, `/me` con y sin token).
- La UI en `http://localhost:5173` completa registro → login → ver perfil → logout contra el backend local.
