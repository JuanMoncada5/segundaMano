# Modelo de Agregados — UniSegundaMano

Este documento describe los Agregados del dominio, sus raíces y los invariantes que protegen, siguiendo el patrón de Domain-Driven Design aplicado en el proyecto.
## Los 3 Agregados

### Agregado 1 — UniCambista (raíz)

**Contiene:** `RachaConfianza` (Value Object)

**Invariante que protege:** la reputación de un UniCambista solo puede modificarse como resultado de una calificación válida; nadie puede editar `RachaConfianza` directamente desde afuera.

### Agregado 2 — Publicacion (raíz)

**Contiene:** `Oferta` (entidad interna, no accesible desde afuera del agregado), `EstadoPublicacion` (enum), `VigenciaAcademica` (Value Object opcional)

**Referencia (no contiene):** `UniCambista` (dueño de la publicación)

**Invariantes que protege:**
- Una `Oferta` solo puede crearse a través de `Publicacion.recibirOferta(...)`; el método `Oferta.crear(...)` es package-private, por lo que ninguna clase externa puede invocarlo directamente.
- La contraoferta no puede ser menor al 50% del precio original.
- Solo se puede ofertar sobre una Publicación en estado `ACTIVA`.

### Agregado 3 — Transaccion (raíz)

**Contiene:** `EncuentroSeguro` (Value Object), `EstadoTransaccion` (enum)

**Referencia (no contiene):** `Publicacion` y `UniCambista` (comprador y vendedor)

**Invariantes que protege:**
- Una Transacción solo puede iniciarse a partir de una `Oferta` en estado `ACEPTADA`.
- No pasa a `COMPLETADA` hasta que ambos UniCambistas (comprador y vendedor) la confirmen por separado.
- El monto acordado queda congelado en el momento de iniciar la Transacción; no se ve afectado por cambios posteriores en la Publicación o la Oferta original.

## Por qué son 3 Agregados y no uno solo

`Transaccion` y `UniCambista` **no** viven dentro de `Publicacion` porque cada uno tiene su propio ciclo de vida independiente:

- Una `Publicacion` puede eliminarse lógicamente sin que eso afecte a las Transacciones ya completadas sobre ella (se conservan por trazabilidad).
- Un `UniCambista` sigue existiendo y acumulando `RachaConfianza` sin importar qué pase con una Publicación puntual.

Por eso se referencian entre sí (flechas punteadas `..>`), en vez de contenerse (flechas de rombo relleno `*--`).
