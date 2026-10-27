# EjeLaboral — Proyecto base con metodología SDD

Proyecto base de autenticación construido con **Spec-Driven Development (SDD)**:
la especificación manda, el código la implementa y los tests la verifican.

| Capa | Tecnología |
|------|------------|
| Backend | Java 21 · Spring Boot 3.5 · Spring Security · JWT (jjwt) |
| Hash de contraseñas | BCrypt (strength 12) |
| ORM / BD | Spring Data JPA (Hibernate) · MySQL 8 |
| Frontend | React + Vite (interfaz de login y registro) |
| Tests | JUnit 5 + TestRestTemplate (H2 en memoria, sin MySQL para testear) |

---

## 📐 Metodología SDD — cómo se trabaja

```
1. SPEC      specs/NNN-nombre/spec.md        → qué y por qué (historias, criterios de aceptación)
2. PLAN      specs/NNN-nombre/plan.md        → cómo (decisiones técnicas, arquitectura)
3. CONTRATO  specs/NNN-nombre/contracts/     → API exacta antes de codearla
4. TASKS     specs/NNN-nombre/tasks.md       → desglose trazable (cada tarea → FR/US)
5. CÓDIGO    backend/ y frontend/            → implementa el contrato, nada más
6. TESTS     backend/src/test                → cada criterio de aceptación verificable
```

**Regla de oro:** ninguna funcionalidad nueva empieza por código; empieza creando su spec.
La `constitution.md` (`.specify/memory/`) define los principios innegociables.

Estructura del repo:

```
ejelaboral/
├── .specify/memory/constitution.md     # principios del proyecto
├── specs/001-autenticacion/            # spec, plan, modelo de datos, contrato API, tareas
├── backend/                            # API Spring Boot (Java 21)
├── frontend/                           # SPA React + Vite
└── docker-compose.yml                  # MySQL alternativo (puerto 3307)
```

---

## 🚀 Arranque

### 0) Base de datos

El proyecto usa MySQL local (servicio `MySQL80`). Solo necesitas crear la base:

```sql
CREATE DATABASE IF NOT EXISTS ejelaboral CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
```

*¿No tienes MySQL o prefieres aislarte?* `docker compose up -d` levanta MySQL en el
puerto **3307** y luego arrancas el backend con `DB_PORT=3307 DB_USERNAME=ejelaboral DB_PASSWORD=ejelaboral`.

### 1) Backend (puerto 8080)

```powershell
cd backend
.\mvnw.cmd spring-boot:run
```

Con credenciales distintas a las por defecto:

```powershell
$env:DB_PASSWORD = "tu-clave"; $env:APP_JWT_SECRET = "una-clave-larga-aleatoria..."; .\mvnw.cmd spring-boot:run
```

> En esta máquina ya está creado `scripts/run-backend.local.ps1` (gitignore, con las credenciales locales
> del usuario `ejelaboral`): `powershell -ExecutionPolicy Bypass -File scripts\run-backend.local.ps1`

> El primer `mvnw` descarga Maven automáticamente. Maven ya está instalado además en
> `C:\Users\ACER\tools\apache-maven-3.9.9` (opcional: añadir `...\bin` al PATH).

### 2) Frontend (puerto 5173)

```powershell
cd frontend
npm install
npm run dev
```

Abre **http://localhost:5173**: regístrate, inicia sesión y verás tu perfil (protegido con JWT).

### 3) Tests del backend (no requieren MySQL)

```powershell
cd backend
.\mvnw.cmd test
```

---

## 🔌 API (resumen — contrato completo en `specs/001-autenticacion/contracts/auth-api.md`)

| Método | Ruta | Auth | Descripción |
|--------|------|------|-------------|
| POST | `/api/auth/register` | pública | Crea cuenta → `201` + JWT |
| POST | `/api/auth/login` | pública | Valida credenciales → `200` + JWT |
| GET | `/api/auth/me` | 🔒 Bearer | Perfil del usuario del token |

Errores en formato único: `{ status, error, message, fieldErrors, timestamp }`.

## ⚙️ Variables de entorno

| Variable | Default | Descripción |
|----------|---------|-------------|
| `DB_HOST` / `DB_PORT` / `DB_NAME` | `localhost` / `3306` / `ejelaboral` | MySQL |
| `DB_USERNAME` / `DB_PASSWORD` | `root` / *(vacía)* | Credenciales |
| `APP_JWT_SECRET` | placeholder de desarrollo | Secret HS256 (≥ 32 bytes) — **cámbialo en producción** |
| `APP_JWT_EXPIRATION_MS` | `3600000` | Vida del token (1 h) |
| `APP_CORS_ORIGINS` | orígenes locales | Orígenes CORS permitidos |

## 🔐 Notas de seguridad

- Las contraseñas se guardan como hash BCrypt (nunca texto plano).
- JWT firmado HS256 con expiración; la sesión es stateless.
- Todo endpoint exige autenticación salvo `register` y `login`.

## 🗺️ Siguientes pasos (backlog en `specs/001-autenticacion/tasks.md`)

Refresh tokens · recuperación de contraseña · verificación de email · roles/autorización ·
rate limiting · CI. Cada uno comienza con una nueva spec.
