# Entrega 1 — Modelado del Dominio · UniSegundaMano

Programación Avanzada · Universidad del Quindío · Docente: Valentina Oviedo Sánchez

## 1. Nicho y lenguaje ubicuo

**Nicho:** marketplace de segunda mano exclusivo para estudiantes de una misma universidad, donde cada persona (UniCambista) es a la vez comprador y vendedor, verificada con correo institucional y con una reputación ligada a su identidad.

**Términos del lenguaje ubicuo** (detalle y anti-patrones en `glosario-lenguaje-obicuo.md`): `UniCambista`, `Trueque`, `EncuentroSeguro`, `RachaConfianza`, `VigenciaAcademica`. En el código también se usan `Publicacion`, `Oferta` y `Transaccion`.

## 2. Reglas de negocio innegociables

1. Un UniCambista no puede crear Publicaciones hasta registrarse y verificar su correo institucional (`@uqvirtual.edu.co` o `@uniquindio.edu.co`).
2. Cuando una Publicación está en proceso de compra, sus atributos y su valor no pueden modificarse.
3. Una Transacción no pasa a Completada hasta que comprador y vendedor la confirmen.
4. Una Publicación con Transacción activa solo se elimina de forma lógica, nunca física.
5. Un UniCambista solo puede calificar Transacciones en las que participó.
6. La contraoferta no puede ser menor al 50% del precio original.
7. Una Publicación expira a los 60 días sin actividad (con o sin VigenciaAcademica).
8. Si la RachaConfianza baja de 3.0 estrellas tras 5 o más Transacciones, se muestra una advertencia visible.

| Regla | Dónde se protege en el código |
|---|---|
| 1 | `UniCambista.registrar` (dominio institucional) y `Publicacion.publicar` (`puedeCrearPublicacion`) |
| 2 | `Publicacion.editar` (solo si está `ACTIVA` y solo el dueño) |
| 3 | `Transaccion.confirmar` |
| 4 | `Publicacion.eliminarLogicamente` |
| 5 | `UniCambista.calificar` y `Transaccion.registrarCalificacionDe` |
| 6 | `Oferta.validarMontoMinimo` (al crear y al contraofertar) |
| 7 | `Publicacion.expirarSiCorresponde` y `ExpirarPublicacionesUseCase` |
| 8 | `RachaConfianza.debeMostrarAdvertencia` |

## 3. Clasificación Entidad / Value Object

Las 3 pruebas: **identidad** (¿dos objetos con los mismos datos siguen siendo distintos?), **reemplazo** (¿puedo cambiarlo por otro igual sin que importe?) y **ciclo de vida** (¿nace, cambia de estado y termina?).

| Concepto | Tipo | Identidad | Reemplazo | Ciclo de vida |
|---|---|---|---|---|
| UniCambista | Entidad (raíz Agregado 1) | Sí: su correo institucional (normalizado en minúsculas) | No: no se puede cambiar por otro con los mismos datos | Se registra, se verifica y acumula reputación |
| Publicacion | Entidad (raíz Agregado 2) | Sí: `codigo` (UUID) | No | ACTIVA → EN_PROCESO / EXPIRADA / ELIMINADA |
| Oferta | Entidad interna del Agregado 2 | Sí: `id` | No | PENDIENTE → ACEPTADA / RECHAZADA / CONTRAOFERTADA → ACEPTADA (si el comprador acepta la contraoferta). Si se acepta otra Oferta de la misma Publicación, las abiertas pasan a RECHAZADA |
| Transaccion | Entidad (raíz Agregado 3) | Sí: `id` | No | PENDIENTE → COMPLETADA |
| RachaConfianza | Value Object (record) | No | Sí: al calificar se reemplaza por una nueva con los valores actualizados | No tiene ciclo propio |
| EncuentroSeguro | Value Object (record: `zona` + `referencia` opcional) | No: dos "Biblioteca - Central" son lo mismo | Sí | No |
| ZonaCampus | Value Object (enum) | No | Sí | No (lista cerrada de zonas del Campus) |
| VigenciaAcademica | Value Object (record) | No: curso + semestre iguales son lo mismo | Sí | No |
| EstadoPublicacion, EstadoOferta, EstadoTransaccion | Value Objects (enum) | No | Sí | No (son valores, el ciclo lo tiene la entidad que los usa) |

## 4. Agregados e invariantes

Detalle completo, diagrama Mermaid y las invariantes de cada raíz en `docs/agregados-unisegundamano.md`.

- **Agregado 2 — Publicacion** (contiene `Oferta`): 9 invariantes. Protegidas en `Publicacion.publicar`, `editar`, `recibirOferta`, `aceptarOferta`, `rechazarOferta`, `contraofertar`, `aceptarContraoferta`, `eliminarLogicamente` y `expirarSiCorresponde`, en `Oferta.crear` (package-private) y en los métodos de `Oferta` que solo puede invocar `Publicacion` (`aceptar`, `rechazar`, `contraofertar`, `aceptarContraoferta`, `descartar`). `marcarEnProceso()` y `cerrarNegociacionCon()` son privados: solo la raíz cambia su estado.
- **Agregado 3 — Transaccion**: 7 invariantes. Protegidas en `Transaccion.iniciar`, `asignarEncuentroSeguro`, `confirmar`, en el record `EncuentroSeguro` y en el enum `ZonaCampus`.
- **Agregado 1 — UniCambista**: 5 invariantes. Protegidas en `UniCambista.registrar` (correo institucional normalizado), `UniCambista.calificar` (único camino para cambiar la `RachaConfianza`, que se modifica solo con un método privado) y en `Transaccion.registrarCalificacionDe` (una calificación por participante).

Las dos raíces principales para esta entrega son `Publicacion` y `Transaccion`: tienen ciclos de vida independientes (una Publicación puede eliminarse lógicamente sin afectar las Transacciones ya completadas).

## 5. Excepción de dominio

`ReglaDominioException` (`domain/exception`). Se usa en todas las entidades y Value Objects y en los casos de uso; no se lanza ninguna otra excepción propia. Incluso el texto inválido de una zona (`ZonaCampus.desde`) se traduce a `ReglaDominioException` en vez de dejar escapar un `IllegalArgumentException`.

## 6. Casos de uso

| # | Caso de uso | Actor | Repository que necesita | Estado |
|---|---|---|---|---|
| 1 | RegistrarUniCambista | Comprador y vendedor | UniCambistaRepository | Documentado (la regla ya está en `UniCambista.registrar`) |
| 2 | VerificarCorreoUniCambista | Comprador y vendedor | UniCambistaRepository | Documentado |
| 3 | PublicarArticulo | Vendedor | PublicacionRepository, UniCambistaRepository | Programado |
| 4 | OfertarArticulo | Comprador | PublicacionRepository, UniCambistaRepository | Programado |
| 5 | AceptarOferta (inicia la Transacción) | Vendedor | PublicacionRepository, TransaccionRepository, UniCambistaRepository | Programado |
| 6 | RechazarOContraofertar | Vendedor | PublicacionRepository, UniCambistaRepository | Documentado (la regla ya está en `Publicacion.rechazarOferta` / `contraofertar`) |
| 6b | AceptarContraoferta | Comprador | PublicacionRepository, TransaccionRepository, UniCambistaRepository | Documentado (la regla ya está en `Publicacion.aceptarContraoferta`) |
| 7 | AsignarEncuentroSeguro | Comprador y vendedor | TransaccionRepository, UniCambistaRepository | Programado |
| 8 | ConfirmarTransaccion | Comprador y vendedor | TransaccionRepository, UniCambistaRepository | Programado |
| 9 | CalificarUniCambista | Comprador y vendedor | TransaccionRepository, UniCambistaRepository | Documentado (la regla ya está en `UniCambista.calificar`) |
| 10 | ExpirarPublicaciones | Sistema (tarea periódica) | PublicacionRepository | Programado |

Los casos de uso solo orquestan: reciben un DTO, buscan las entidades en los Repository (por correo o código), llaman al método del dominio, guardan y devuelven un DTO de respuesta. Ninguna regla de negocio vive en ellos.

## 7. Repositories

Las interfaces viven en `domain/repository` y las implementaciones en `infrastructure/persistence`:

- `PublicacionRepository` (`buscarPorCodigo`, `guardar`, `listarActivas`) → `PublicacionRepositoryEnMemoria` (HashMap).
- `TransaccionRepository` (`buscarPorId`, `guardar`, `listarPorParticipante`) → `TransaccionRepositoryEnMemoria` (HashMap).
- `UniCambistaRepository` (`buscarPorCorreo`, `guardar`) → `UniCambistaRepositoryEnMemoria` (HashMap; busca con el correo normalizado, así "Laura@UQvirtual.edu.co" encuentra a "laura@uqvirtual.edu.co").

## 8. DTOs (`application/dto`)

Implementados como `record` de Java (inmutables). Los **Request** solo llevan identificadores (correo, código, id) y datos simples; los **Response** tienen un método estático `desde(entidad)` que hace el mapeo. Así el dominio nunca queda expuesto hacia afuera.

**PublicarArticuloRequest** → `PublicarArticuloUseCase` → `Publicacion.publicar(...)`

| Campo | Por qué es necesario |
|---|---|
| `correoDueno` | Identifica al UniCambista dueño (su correo es su identidad) para verificar que puede publicar |
| `titulo` | Es lo que ven los demás UniCambistas en el catálogo |
| `valor` | Base para validar el mínimo del 50% en las Ofertas y contraofertas |
| `curso`, `semestre` | Opcionales: si vienen los dos se asocia una `VigenciaAcademica` |

**OfertarArticuloRequest** → `OfertarArticuloUseCase` → `Publicacion.recibirOferta(...)`

| Campo | Por qué es necesario |
|---|---|
| `codigoPublicacion` | Identifica la Publicación (raíz del agregado) que recibe la Oferta |
| `correoOferente` | Identifica quién ofrece; no puede ser el dueño |
| `montoPropuesto` | Es el dato que se valida contra el 50% del precio |

**AceptarOfertaRequest** → `AceptarOfertaUseCase` → `Publicacion.aceptarOferta(...)` + `Transaccion.iniciar(...)`

| Campo | Por qué es necesario |
|---|---|
| `codigoPublicacion` | Identifica la Publicación que contiene la Oferta |
| `idOferta` | Identifica la Oferta a aceptar dentro del agregado |
| `correoDueno` | Solo el dueño puede aceptar; el dominio lo valida |

**AsignarEncuentroSeguroRequest** → `AsignarEncuentroSeguroUseCase` → `Transaccion.asignarEncuentroSeguro(...)`

| Campo | Por qué es necesario |
|---|---|
| `idTransaccion` | Identifica la Transacción |
| `correoUniCambista` | Solo el comprador o el vendedor pueden acordar el lugar |
| `zona` | Texto de una `ZonaCampus` (BIBLIOTECA, CANCHAS, EDIFICIOS, PORTERIAS, CAFETERIAS) |
| `referencia` | Detalle opcional dentro de la zona (ej. "Central", "Bloque A") |

**ConfirmarTransaccionRequest** → `ConfirmarTransaccionUseCase` → `Transaccion.confirmar(...)`

| Campo | Por qué es necesario |
|---|---|
| `idTransaccion` | Identifica la Transacción a confirmar |
| `correoUniCambista` | Debe ser el comprador o el vendedor; si no, el dominio rechaza la confirmación |

**PublicacionResponse** → sale de `Publicacion` tras publicar

| Campo | Por qué es necesario |
|---|---|
| `codigo` | Para ofertar o aceptar Ofertas después |
| `correoDueno` | Muestra quién vende |
| `titulo`, `valor` | Datos que se muestran en el catálogo |
| `estado` | ACTIVA, EN_PROCESO, EXPIRADA o ELIMINADA |
| `etiquetaVigenciaAcademica` | Ej. "Cálculo I - 2026-2"; puede ser nula |

**OfertaResponse** → sale de `Oferta` tras ofertar

| Campo | Por qué es necesario |
|---|---|
| `id` | Para que el dueño pueda aceptarla, rechazarla o contraofertarla |
| `codigoPublicacion` | Publicación a la que pertenece |
| `correoOferente` | Quién hizo la Oferta |
| `montoPropuesto` | Valor ofrecido (o el de la contraoferta) |
| `estado` | PENDIENTE, ACEPTADA, RECHAZADA o CONTRAOFERTADA |

**TransaccionResponse** → sale de `Transaccion` tras iniciar, asignar EncuentroSeguro o confirmar

| Campo | Por qué es necesario |
|---|---|
| `id` | Para que el cliente pueda asignar el EncuentroSeguro y confirmarla después |
| `codigoPublicacion` | Publicación de la que viene |
| `correoComprador`, `correoVendedor` | Quiénes participan |
| `montoAcordado` | Muestra el valor congelado al iniciar |
| `estado` | Indica si sigue PENDIENTE o ya está COMPLETADA |
| `zonaEncuentro`, `referenciaEncuentro` | Dónde coordinan la entrega (nulos hasta que se acuerde) |

## 9. Estructura de paquetes

```
com.uniquindio.segundaMano
├── domain
│   ├── entity            UniCambista, Publicacion, Oferta, Transaccion
│   ├── valueObject       EstadoOferta, EstadoPublicacion, EstadoTransaccion, ZonaCampus,
│   │                     RachaConfianza, EncuentroSeguro, VigenciaAcademica
│   ├── exception         ReglaDominioException
│   └── repository        PublicacionRepository, TransaccionRepository, UniCambistaRepository
├── application
│   ├── dto               PublicarArticuloRequest, OfertarArticuloRequest, AceptarOfertaRequest,
│   │                     AsignarEncuentroSeguroRequest, ConfirmarTransaccionRequest,
│   │                     PublicacionResponse, OfertaResponse, TransaccionResponse
│   └── usecase           PublicarArticulo, OfertarArticulo, AceptarOferta, AsignarEncuentroSeguro,
│                         ConfirmarTransaccion, ExpirarPublicaciones
└── infrastructure
    └── persistence       PublicacionRepositoryEnMemoria, TransaccionRepositoryEnMemoria,
                          UniCambistaRepositoryEnMemoria
```

El paquete `domain` no importa Spring, JPA ni Lombok.

## 10. Pruebas unitarias (patrón Arrange-Act-Assert)

| Clase de prueba | Qué cubre | Pruebas |
|---|---|---|
| RachaConfianzaTest | Value Object | 2 |
| EncuentroSeguroTest | Value Object | 3 |
| UniCambistaTest | Entidad (identidad, correo institucional y normalización) | 3 |
| UniCambistaCalificacionTest | Entidad / Agregado 1 (calificar, una sola vez) | 4 |
| PublicacionOfertaAgregadoTest | Invariantes del Agregado Publicacion (50%, dueño) | 2 |
| PublicacionInvariantesTest | Invariantes del Agregado Publicacion (estados de la Oferta, contraoferta, publicar, editar, eliminar, expirar) | 12 |
| TransaccionAgregadoTest | Invariantes del Agregado Transaccion (incluye EncuentroSeguro obligatorio y solo participantes lo asignan) | 8 |
| FlujoTransaccionCompletoTest | Flujo completo de dominio | 1 |
| CasosDeUsoTest | Casos de uso con DTOs y los Repository en memoria | 8 |
| **Total** | | **43** |

## 11. Organización en Git

Una rama por caso de uso o tema, integradas a `master` por merge:

- **Dominio:** `feature/publicar-articulo`, `feature/negociar-oferta`, `feature/zona-campus`, `feature/invariantes-publicacion`, `feature/invariantes-transaccion`, `fix/invariantes-dominio`.
- **Casos de uso y repositorios:** `feature/useCaseYRepository`, `feature/repository-transaccion`, `feature/ofertar-articulo`, `feature/aceptar-oferta`, `feature/confirmar-transaccion-usecase`, `feature/calificar-unicambista`, `feature/dtos-casos-de-uso`.
- **Estructura:** `feature/nuevas-carpetas`, `feature/estructura-paquetes`, `refactor/infrastructure`.
- **Pruebas:** `feature/confirmar-transaccion`, `test/casos-de-uso`.
- **Documentación:** `docs/diagrama-agregados`, `docs/entrega1`, `docs/DiagramaEntrega1`, `docs/invariantes`.
