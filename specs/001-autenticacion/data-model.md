# Modelo de Datos — 001 Autenticación

## Entidad: User (tabla `users`)

| Columna | Tipo MySQL | Restricciones | Mapeo JPA |
|---------|------------|----------------|-----------|
| `id` | BIGINT | PK, AUTO_INCREMENT | `@Id @GeneratedValue(IDENTITY)` |
| `email` | VARCHAR(180) | NOT NULL, UNIQUE (case-insensitive) | `@Column(nullable=false)` |
| `password_hash` | VARCHAR(255) | NOT NULL | `@Column(name="password_hash")` |
| `full_name` | VARCHAR(120) | NOT NULL | `@Column(name="full_name")` |
| `role` | VARCHAR(20) | NOT NULL, default `USER` | `@Enumerated(STRING)` |
| `enabled` | BOOLEAN | NOT NULL, default TRUE | `@Column(nullable=false)` |
| `created_at` | TIMESTAMP(6) | NOT NULL, no actualizable | `@PrePersist` |

## DDL de referencia (Hibernate `ddl-auto: update` lo genera)

```sql
CREATE TABLE users (
    id            BIGINT       NOT NULL AUTO_INCREMENT,
    email         VARCHAR(180) NOT NULL,
    password_hash VARCHAR(255) NOT NULL,
    full_name     VARCHAR(120) NOT NULL,
    role          VARCHAR(20)  NOT NULL,
    enabled       BOOLEAN      NOT NULL,
    created_at    TIMESTAMP(6) NOT NULL,
    PRIMARY KEY (id),
    CONSTRAINT uk_users_email UNIQUE (email)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;
```

## Enumeraciones

- `Role`: `USER`, `ADMIN` (reservado; en esta spec solo se asigna `USER`).

## Relaciones

Ninguna en esta iteración (proyecto base). Extensiones previstas en backlog:
`refresh_tokens` (N:1 con users), `password_resets` (1:1 con users).

## Decisiones

- **Tabla `users`** (plural) para evitar colisiones con palabras reservadas.
- **Unicidad en BD** (`UNIQUE(email)`) además de la comprobación en servicio: la BD es la última línea de defensa.
- **`password_hash`** nombra explícitamente su contenido: nunca debe existir una columna `password` con texto plano (Constitución IV).
- **Email normalizado** a minúsculas en la capa de servicio (`AuthService.register`).
