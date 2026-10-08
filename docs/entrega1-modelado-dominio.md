# Entrega 1 — Modelado del Dominio · UniSegundaMano

Programación Avanzada · Universidad del Quindío · Docente: Valentina Oviedo Sánchez

## 1. Nicho y lenguaje ubicuo

**Nicho:** marketplace de segunda mano exclusivo para estudiantes de una misma universidad, donde cada persona (UniCambista) es a la vez comprador y vendedor, verificada con correo institucional y con una reputación ligada a su identidad.

**Términos del lenguaje ubicuo** (detalle y anti-patrones en `glosario-lenguaje-obicuo.md`): `UniCambista`, `Trueque`, `EncuentroSeguro`, `RachaConfianza`, `VigenciaAcademica`. En el código también se usan `Publicacion`, `Oferta` y `Transaccion`.

## 2. Reglas de negocio innegociables

1. Un UniCambista no puede crear Publicaciones hasta registrarse y verificar su correo institucional.
2. Cuando una Publicación está en proceso de compra, sus atributos y su valor no pueden modificarse.
3. Una Transacción no pasa a Completada hasta que comprador y vendedor la confirmen.
4. Una Publicación con Transacción activa solo se elimina de forma lógica, nunca física.
5. Un UniCambista solo puede calificar Transacciones en las que participó.
6. La contraoferta no puede ser menor al 50% del precio original.
7. Una Publicación expira a los 60 días sin actividad (con o sin VigenciaAcademica).
8. Si la RachaConfianza baja de 3.0 estrellas tras 5 o más Transacciones, se muestra una advertencia visible.

## 3. Clasificación Entidad / Value Object

Las 3 pruebas: **identidad** (¿dos objetos con los mismos datos siguen siendo distintos?), **reemplazo** (¿puedo cambiarlo por otro igual sin que importe?) y **ciclo de vida** (¿nace, cambia de estado y termina?).

| Concepto | Tipo | Identidad | Reemplazo | Ciclo de vida |
|---|---|---|---|---|
| UniCambista | Entidad (raíz Agregado 1) | Sí: su correo institucional | No: no se puede cambiar por otro con los mismos datos | Se registra, se verifica y acumula reputación |
| Publicacion | Entidad (raíz Agregado 2) | Sí: `codigo` (UUID) | No | ACTIVA → EN_PROCESO / EXPIRADA / ELIMINADA |
| Oferta | Entidad interna del Agregado 2 | Sí: `id` | No | PENDIENTE → ACEPTADA / RECHAZADA / CONTRAOFERTADA → ACEPTADA (si el comprador acepta la contraoferta) |
| Transaccion | Entidad (raíz Agregado 3) | Sí: `id` | No | PENDIENTE → COMPLETADA |
| RachaConfianza | Value Object (record) | No | Sí: al calificar se reemplaza por una nueva con los valores actualizados | No tiene ciclo propio |
| EncuentroSeguro | Value Object (record: `zona` + `referencia` opcional) | No: dos "Biblioteca - Central" son lo mismo | Sí | No |
| ZonaCampus | Value Object (enum) | No | Sí | No (lista cerrada de zonas del Campus) |
| VigenciaAcademica | Value Object (record) | No: curso + semestre iguales son lo mismo | Sí | No |
| EstadoPublicacion, EstadoOferta, EstadoTransaccion | Value Objects (enum) | No | Sí | No (son valores, el ciclo lo tiene la entidad que los usa) |

## 4. Agregados e invariantes

Detalle completo, diagrama Mermaid y las invariantes de cada raíz en `docs/agregados-unisegundamano.md`.

- **Agregado 2 — Publicacion** (contiene `Oferta`): 6 invariantes. Protegidas en `Publicacion.publicar`, `recibirOferta`, `aceptarOferta`, `contraofertar`, `aceptarContraoferta`, `eliminarLogicamente`, en `Oferta.crear` (package-private) y en los métodos de `Oferta` que solo puede invocar `Publicacion`. `marcarEnProceso()` es privado: solo la raíz cambia su estado.
- **Agregado 3 — Transaccion**: 6 invariantes. Protegidas en `Transaccion.iniciar`, `confirmar`, `asignarEncuentroSeguro`, en el record `EncuentroSeguro` y en el enum `ZonaCampus`.
- **Agregado 1 — UniCambista**: 4 invariantes. Protegidas en `UniCambista.calificar` (único camino para cambiar la `RachaConfianza`, que se modifica solo con un método privado) y en `Transaccion.registrarCalificacionDe` (una calificación por participante).

Las dos raíces principales para esta entrega son `Publicacion` y `Transaccion`: tienen ciclos de vida independientes (una Publicación puede eliminarse lógicamente sin afectar las Transacciones ya completadas).

## 5. Excepción de dominio

`ReglaDominioException` (`domain/exception`). Se usa en todas las entidades y Value Objects y en los casos de uso; no se lanza ninguna otra excepción propia.

## 6. Casos de uso

| # | Caso de uso | Actor | Repository que necesita | Estado |
|---|---|---|---|---|
| 1 | RegistrarUniCambista | Comprador y vendedor | UniCambistaRepository | Documentado |
| 2 | VerificarCorreoUniCambista | Comprador y vendedor | UniCambistaRepository | Documentado |
| 3 | PublicarArticulo | Vendedor | PublicacionRepository | Programado |
| 4 | OfertarArticulo | Comprador | PublicacionRepository | Programado |
| 5 | AceptarOferta (inicia la Transacción) | Vendedor | PublicacionRepository, TransaccionRepository | Programado |
| 6 | RechazarOContraofertar | Vendedor | PublicacionRepository | Documentado (la regla ya está en `Publicacion.rechazarOferta` / `contraofertar`) |
| 6b | AceptarContraoferta | Comprador | PublicacionRepository, TransaccionRepository | Documentado (la regla ya está en `Publicacion.aceptarContraoferta`) |
| 7 | ConfirmarTransaccion | Comprador y vendedor | TransaccionRepository | Programado |
| 8 | CalificarUniCambista | Comprador y vendedor | TransaccionRepository, UniCambistaRepository | Documentado (la regla ya está en `UniCambista.calificar`) |

Los casos de uso solo orquestan: buscan en el Repository, llaman al método del dominio y guardan. Ninguna regla de negocio vive en ellos.

## 7. Repositories

- `PublicacionRepository` (`buscarPorCodigo`, `guardar`, `listarActivas`) → `PublicacionRepositoryEnMemoria` (HashMap).
- `TransaccionRepository` (`buscarPorId`, `guardar`, `listarPorParticipante`) → `TransaccionRepositoryEnMemoria` (HashMap).

## 8. DTOs diseñados (`application/dto`)

**PublicarArticuloRequest** → mapea a `Publicacion.publicar(...)` (caso de uso 3)

| Campo | Por qué es necesario |
|---|---|
| `correoDueno` | Identifica al UniCambista dueño (su correo es su identidad) para verificar que puede publicar |
| `titulo` | Es lo que ven los demás UniCambistas en el catálogo |
| `valor` | Base para validar el mínimo del 50% en las contraofertas |

**OfertarArticuloRequest** → mapea a `Publicacion.recibirOferta(...)` (caso de uso 4)

| Campo | Por qué es necesario |
|---|---|
| `codigoPublicacion` | Identifica la Publicación (raíz del agregado) que recibe la Oferta |
| `correoOferente` | Identifica quién ofrece; no puede ser el dueño |
| `montoPropuesto` | Es el dato que se valida contra el 50% del precio |

**ConfirmarTransaccionRequest** → mapea a `Transaccion.confirmar(...)` (caso de uso 7)

| Campo | Por qué es necesario |
|---|---|
| `idTransaccion` | Identifica la Transacción a confirmar |
| `correoUniCambista` | Debe ser el comprador o el vendedor; si no, el dominio rechaza la confirmación |

**TransaccionResponse** → sale de `Transaccion` tras iniciar o confirmar

| Campo | Por qué es necesario |
|---|---|
| `id` | Para que el cliente pueda confirmarla después |
| `montoAcordado` | Muestra el valor congelado al iniciar |
| `estado` | Indica si sigue PENDIENTE o ya está COMPLETADA |
| `encuentroSeguro` | Dónde coordinan la entrega (puede ser nulo al inicio) |

Los DTOs usan identificadores (correo, código) y no objetos de dominio: el caso de uso busca la entidad y el dominio nunca queda expuesto hacia afuera. Por ahora los casos de uso reciben el `UniCambista` como parámetro; cuando se programe `UniCambistaRepository`, lo buscarán por correo.

## 9. Estructura de paquetes

```
com.uniquindio.segundaMano
├── domain
│   ├── entity            UniCambista, Publicacion, Oferta, Transaccion
│   ├── valueObject       EstadoOferta, EstadoPublicacion, EstadoTransaccion, ZonaCampus,
│   │                     RachaConfianza, EncuentroSeguro, VigenciaAcademica
│   ├── exception         ReglaDominioException
│   └── repository        PublicacionRepository, TransaccionRepository
├── application
│   ├── dto               (diseñados en la sección 8)
│   └── usecase           PublicarArticulo, OfertarArticulo, AceptarOferta, ConfirmarTransaccion
└── infrastructure
    └── persistence       PublicacionRepositoryEnMemoria, TransaccionRepositoryEnMemoria
```

El paquete `domain` no importa Spring, JPA ni Lombok.

## 10. Pruebas unitarias (patrón Arrange-Act-Assert)

| Clase de prueba | Qué cubre | Pruebas |
|---|---|---|
| RachaConfianzaTest | Value Object | 2 |
| EncuentroSeguroTest | Value Object | 3 |
| UniCambistaTest | Entidad (identidad y registro) | 2 |
| UniCambistaCalificacionTest | Entidad / Agregado 1 (calificar, una sola vez) | 4 |
| PublicacionOfertaAgregadoTest | Invariantes del Agregado Publicacion (50%, dueño) | 2 |
| PublicacionInvariantesTest | Invariantes del Agregado Publicacion (estados de la Oferta, contraoferta, publicar, eliminar) | 7 |
| TransaccionAgregadoTest | Invariantes del Agregado Transaccion | 6 |
| FlujoTransaccionCompletoTest | Flujo completo de dominio | 1 |
| CasosDeUsoTest | Casos de uso con los Repository en memoria | 4 |
| **Total** | | **31** |

## 11. Organización en Git

Una rama por caso de uso o tema, integradas a `master` por merge: `feature/publicar-articulo`, `feature/negociar-oferta`, `feature/confirmar-transaccion`, `feature/useCaseYRepository`, `feature/nuevas-carpetas` y `docs/diagrama-agregados`. Los casos de uso 4, 5 y la calificación se suben cada uno en su propia rama (`feature/ofertar-articulo`, `feature/aceptar-oferta`, `feature/calificar-unicambista`).
