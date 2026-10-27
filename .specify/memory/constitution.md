# Constitución del Proyecto — EjeLaboral

> Principios innegociables que rigen el desarrollo de este proyecto.
> Toda decisión técnica debe poder justificarse contra esta constitución.
> Versión: 1.0.0 — 2026-10-27

## Principios

### I. Especificaciones primero (Spec-Driven)
Ninguna funcionalidad se implementa sin una especificación aprobada en `specs/NNN-nombre/`.
El ciclo obligatorio es: **spec → plan → tasks → código → tests**. El código existe para
satisfacer la spec, nunca al revés.

### II. Un cambio relevante, una spec
Toda feature nueva o cambio de comportamiento relevante crea/actualiza su spec
(`spec.md`), su plan técnico si aplica (`plan.md`), y su desglose de tareas (`tasks.md`).
Los bugs menores se documentan en el commit, no requieren spec.

### III. Contratos de API estables
`contracts/*.md` es la fuente de verdad de la API. Un endpoint no implementado según su
contrato es un defecto, no una decisión. Cambios incompatibles requieren nueva versión
(`/api/v2/...`) y actualización del contrato **antes** del código.

### IV. Seguridad por defecto
- Las contraseñas JAMÁS se almacenan en texto plano: hash BCrypt (strength ≥ 10).
- Los tokens JWT se firman (HS256) y expiran; el secret vive en una variable de entorno, nunca en el repositorio de producción.
- Todo endpoint es autenticado por defecto; lo público debe listarse explícitamente.
- Toda entrada se valida en el borde (validación declarativa).

### V. Pruebas trazables a la spec
Cada criterio de aceptación (AC) de `spec.md` debe poder verificarse con al menos una
prueba automatizada o una verificación manual documentada. Si no hay prueba, no está
terminado.

### VI. Simplicidad (YAGNI)
Implementar lo mínimo que satisface la spec. Ninguna abstracción, dependencia o
configuración "por si acaso". La simplicidad se revisa en cada PR.

## Gobernanza
- Enmiendas a esta constitución se registran subiendo la versión y describiendo el cambio en el commit.
- Ante conflicto entre prisa y constitución, gana la constitución.
