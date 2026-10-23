# Persistencia con H2 y JPA — UniSegundaMano

Hasta ahora los repositories guardaban en un `HashMap` (se pierde todo al apagar). Ahora hay una segunda
implementación que guarda en una base de datos H2. **No se tocó ningún caso de uso ni ninguna regla del dominio.**

## Qué cambió (y qué no)

| Capa | Cambio |
|---|---|
| `domain` | Solo se agregaron métodos `reconstruir(...)` (para armar un objeto desde lo guardado) y dos getters. Ninguna regla cambió. Sigue sin anotaciones ni Spring. |
| `application` | Nada. Los casos de uso siguen hablando con las interfaces `...Repository`. |
| `infrastructure/persistence` | Nuevo paquete `jpa` con la implementación real. Las clases `...EnMemoria` se conservan (los tests las usan con `new`). |

## Estructura nueva (`infrastructure/persistence/jpa`)

```
jpa/
├── entity/        UniCambistaEntity, PublicacionEntity, OfertaEntity, TransaccionEntity
│                  RachaConfianzaEmbeddable, VigenciaAcademicaEmbeddable, EncuentroSeguroEmbeddable
├── mapper/        UniCambistaPersistenceMapper, PublicacionPersistenceMapper, TransaccionPersistenceMapper
├── UniCambistaJpaDataRepository, PublicacionJpaDataRepository,
│   OfertaJpaDataRepository, TransaccionJpaDataRepository     (interfaces vacías: Spring escribe el SQL)
└── UniCambistaRepositoryJpa, PublicacionRepositoryJpa, TransaccionRepositoryJpa   (@Repository)
```

## ¿Por qué dos clases: `Publicacion` y `PublicacionEntity`?

| `Publicacion` (dominio) | `PublicacionEntity` (JPA) |
|---|---|
| Constructor privado y validado | Constructor vacío obligatorio |
| Sin setters | Con setters (para la BD) |
| Sin anotaciones, tiene reglas | Solo datos, usa `@Entity` |

Si anotáramos `Publicacion` con `@Entity` romperíamos sus invariantes solo para contentar a la BD.
El **mapper** es el "bodeguero" que traduce entre las dos.

## Cómo se guardan los conceptos del dominio

| Dominio | En la base de datos |
|---|---|
| `UniCambista` (raíz) | tabla `uni_cambistas`; `RachaConfianza` (Value Object) → 2 columnas (`@Embedded`) |
| `Publicacion` (raíz) | tabla `publicaciones`; `VigenciaAcademica` → 2 columnas; `estado` como texto (`@Enumerated(STRING)`) |
| `Oferta` (entidad interna) | tabla `ofertas`, con el código de su publicación |
| `Transaccion` (raíz) | tabla `transacciones`; `EncuentroSeguro` → 2 columnas |

Los Agregados se referencian **por id** (correo, código), no por objeto completo: así cada raíz conserva su propio ciclo de vida.
`Publicacion` y sus `Oferta` se guardan juntas porque son un solo Agregado.

## Cómo correrlo

1. Ejecutar `SegundaManoApplication`. Hibernate crea las tablas solo (`ddl-auto=create-drop`).
2. Abrir `http://localhost:8080/h2-console` con:
   - JDBC URL: `jdbc:h2:mem:segundamanodb`
   - User: `sa` — Password: (vacío)
3. Se ven las tablas `UNI_CAMBISTAS`, `PUBLICACIONES`, `OFERTAS` y `TRANSACCIONES`.

## Perfil MariaDB (los datos sobreviven al apagado)

```
docker run --name mariadb-segundamano -e MARIADB_ROOT_PASSWORD=root -e MARIADB_DATABASE=segundamanodb -p 3307:3306 -d mariadb:11
```

Activar el perfil `mariadb` (en IntelliJ: *Edit Configurations → Active profiles → `mariadb`*, o `--spring.profiles.active=mariadb`).
Usa `application-mariadb.properties` y cambia `ddl-auto` a `update`.

## Por qué `@Repository` está comentado en las clases `...EnMemoria`

Con las dos implementaciones anotadas, Spring vería dos beans para la misma interfaz y no sabría cuál inyectar.
Solo queda activa la `...RepositoryJpa`. Las `...EnMemoria` se siguen usando en los tests con `new`.
