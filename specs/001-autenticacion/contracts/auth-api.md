# Contrato de API — 001 Autenticación

Base URL: `http://localhost:8080` · Content-Type: `application/json` ·
Autenticación: `Authorization: Bearer <accessToken>`

## Formato de error (FR-9)

```json
{
  "status": 400,
  "error": "Bad Request",
  "message": "Hay errores de validación en la solicitud.",
  "fieldErrors": { "email": "Debe ser un email válido" },
  "timestamp": "2026-10-27T12:00:00Z"
}
```

---

## POST /api/auth/register — FR-1, FR-2, FR-3, FR-4 (US1)

**Request**
```json
{ "email": "ana@example.com", "password": "Secreta123", "fullName": "Ana Pérez" }
```

**201 Created**
```json
{
  "accessToken": "eyJhbGciOiJIUzI1NiJ9...",
  "tokenType": "Bearer",
  "user": { "id": 1, "email": "ana@example.com", "fullName": "Ana Pérez", "role": "USER", "createdAt": "2026-10-27T12:00:00Z" }
}
```

**Errores**
| Código | Cuándo |
|--------|--------|
| 400 | Validación fallida (email inválido, password < 8, nombre vacío) |
| 409 | El email ya está registrado |

---

## POST /api/auth/login — FR-5 (US2)

**Request**
```json
{ "email": "ana@example.com", "password": "Secreta123" }
```

**200 OK** → mismo cuerpo que register.

**Errores**
| Código | Cuándo |
|--------|--------|
| 400 | Cuerpo ausente o malformado |
| 401 | Credenciales incorrectas (mensaje genérico: "Email o contraseña incorrectos") |

---

## GET /api/auth/me — FR-8 (US3) 🔒

**Headers:** `Authorization: Bearer <accessToken>`

**200 OK**
```json
{ "id": 1, "email": "ana@example.com", "fullName": "Ana Pérez", "role": "USER", "createdAt": "2026-10-27T12:00:00Z" }
```

**Errores**
| Código | Cuándo |
|--------|--------|
| 401 | Sin header, token malformado, manipulado o expirado (entry point de seguridad) |

---

## Convenciones

- Endpoints públicos (única excepción): `POST /api/auth/register`, `POST /api/auth/login`. Todo lo demás exige token (FR-11).
- Cambios incompatibles → nueva versión `/api/v2/...` y actualización de este contrato (Constitución III).
