# UniSegundaMano

Marketplace de segunda mano para la comunidad de la Universidad del Quindío. Cada usuario, un **UniCambista**, entra con su correo institucional, publica artículos (libros, calculadoras, batas…), recibe y hace ofertas, y coordina la entrega en un **EncuentroSeguro** dentro del campus. Su **RachaConfianza** (reputación) crece con cada transacción calificada.

**Programación Avanzada · Universidad del Quindío · Docente: Valentina Oviedo Sánchez**

## Integrantes

| Integrante | Usuario de GitHub | Aportes principales |
|---|---|---|
| Juan Moncada | `JuanMoncada5` | Dominio e invariantes, DTOs y casos de uso, búsqueda paginada, ranking, eliminación lógica, códigos HTTP, pruebas avanzadas |
| Juan Manuel Rincón | Juan Manuel Rincon Acosta | Persistencia con JPA y H2, API REST, login con JWT, BCrypt, Swagger, identidad desde el token |

## Tecnologías

Java 21 · Spring Boot 4.1.1 · Gradle · Spring Data JPA · H2 (y perfil opcional MariaDB) · Spring Security con JWT · springdoc-openapi (Swagger) · JUnit 5 y Mockito.

Arquitectura hexagonal / DDD:

```
domain/          entidades, value objects, interfaces de repositorio y excepciones (sin Spring ni JPA)
application/     casos de uso (clases Java normales) y DTOs Request/Response
infrastructure/  persistence (JPA + mappers), rest (controllers + bodies + mappers), security, config
```

## Cómo ejecutar el proyecto

Requisito: **Java 21**.

```bash
./gradlew bootRun
```

> En Windows con Git Bash, si sale *"JAVA_HOME is not set"*, primero indica dónde está Java (por ejemplo, el JDK que descarga IntelliJ):
> `export JAVA_HOME="$HOME/.jdks/ms-21.0.12.1"`

También se puede ejecutar `SegundaManoApplication` desde IntelliJ.

| Qué | Dónde |
|---|---|
| API | `http://localhost:8080` |
| Swagger (documentación y pruebas) | `http://localhost:8080/swagger-ui/index.html` |
| Consola H2 | `http://localhost:8080/h2-console` · JDBC URL `jdbc:h2:mem:segundamanodb` · usuario `sa` · sin contraseña |

La base de datos H2 está en memoria: al arrancar se crean las tablas y se cargan los datos de prueba de `data.sql` y `data-credenciales.sql`. Al apagar se borra todo.

**Perfil MariaDB (opcional):** con `--spring.profiles.active=mariadb` usa `application-mariadb.properties` (los datos sobreviven al apagado). Ver `docs/persistencia-h2.md`.

### Ejecutar las pruebas

```bash
./gradlew test
```

## Usuarios de prueba

Todos usan la contraseña **`Clave123*`** (en la base de datos solo se guarda su hash BCrypt).

| Correo | Rol |
|---|---|
| `ana@uqvirtual.edu.co` | UNICAMBISTA |
| `luis@uqvirtual.edu.co` | UNICAMBISTA |
| `sofia@uqvirtual.edu.co` | UNICAMBISTA |
| `carlos@uniquindio.edu.co` | UNICAMBISTA |
| `admin@uniquindio.edu.co` | ADMIN |

### Cómo autenticarse en Swagger

1. `POST /api/auth/login` con `{ "correoInstitucional": "ana@uqvirtual.edu.co", "contrasena": "Clave123*" }`.
2. Copiar el `token` de la respuesta.
3. Botón **Authorize** 🔒 → pegar el token → **Authorize**.

El token dura 60 minutos (`app.jwt.expiracion-minutos`). La clave secreta y la duración están en `application.properties`, no en el código.

### Datos de prueba (`data.sql`)

| Publicación | Dueño | Valor | Estado | Ofertas |
|---|---|---|---|---|
| PUB-001 Calculadora Casio fx-991 | ana | 150.000 | ACTIVA | 3 |
| PUB-002 Libro Cálculo I - Stewart (Cálculo I, 2026-2) | luis | 80.000 | ACTIVA | 1 |
| PUB-003 Bata de laboratorio talla M | sofia | 40.000 | ACTIVA | 2 |
| PUB-004 Silla de escritorio | ana | 60.000 | ACTIVA | 0 |
| PUB-005 Monitor 24 pulgadas | carlos | 300.000 | ELIMINADA | 4 |

## Endpoints principales

**Públicos** (sin token):

| Método | Ruta | Qué hace |
|---|---|---|
| POST | `/api/auth/login` | Inicia sesión y devuelve el token JWT (correo, rol y vencimiento) |
| POST | `/api/unicambistas` | Registro de un UniCambista (`correoInstitucional`, `nombre`, `contrasena`) |
| PUT | `/api/unicambistas/verificar` | Verifica el correo institucional |

**UNICAMBISTA o ADMIN** (con token). El usuario que actúa **sale del token**, nunca del body:

| Método | Ruta | Qué hace | Actor |
|---|---|---|---|
| POST | `/api/publicaciones` | Publicar un artículo | Vendedor |
| GET | `/api/publicaciones` | Listar publicaciones activas | Todos |
| GET | `/api/publicaciones/buscar?texto=&curso=&valorMin=&valorMax=&pagina=0&tamano=10` | Búsqueda paginada (JPQL), sin eliminadas | Comprador |
| GET | `/api/publicaciones/{codigo}` | Consultar una publicación | Todos |
| DELETE | `/api/publicaciones/{codigo}` | Eliminación lógica (solo el dueño) | Vendedor |
| POST | `/api/publicaciones/{codigo}/ofertas` | Ofertar (mínimo 50% del precio) | Comprador |
| PUT | `/api/publicaciones/{codigo}/ofertas/{idOferta}/aceptar` | Aceptar una oferta (inicia la Transacción) | Vendedor |
| PUT | `/api/transacciones/{id}/encuentro-seguro` | Acordar el EncuentroSeguro (zona del campus) | Comprador / Vendedor |
| PUT | `/api/transacciones/{id}/confirmar` | Confirmar la Transacción (se completa cuando confirman los dos) | Comprador / Vendedor |
| GET | `/api/transacciones?correo=...` | Listar las transacciones de un UniCambista | Comprador / Vendedor |
| GET | `/api/transacciones/{id}` | Consultar una transacción | Comprador / Vendedor |
| GET | `/api/unicambistas/{correo}` | Consultar un UniCambista y su RachaConfianza | Todos |

**Solo ADMIN** (rutas restringidas por rol):

| Método | Ruta | Qué hace |
|---|---|---|
| GET | `/api/admin/reportes/publicaciones-mas-ofertadas?limite=5` | Ranking con JOIN + GROUP BY + COUNT |
| POST | `/api/admin/publicaciones/expirar` | Expira las publicaciones con 60 días o más sin actividad |

### Códigos de respuesta

| Código | Cuándo |
|---|---|
| 200 / 201 / 204 | Éxito (201 al crear, con cabecera `Location`; 204 al eliminar) |
| 400 | Datos inválidos: campos vacíos, JSON mal formado, página negativa, parámetro con tipo incorrecto |
| 401 | Sin token, token inválido o vencido, o credenciales incorrectas |
| 403 | Token válido pero con un rol que no tiene permiso |
| 404 | El recurso o la ruta no existe |
| 405 | Método HTTP no permitido en esa ruta |
| 422 | Se rompió una regla de negocio (por ejemplo, ofertar menos del 50% o eliminar una publicación ajena) |

Todos los errores responden en JSON con el mismo formato: `{ "estado", "error", "mensaje", "detalles" }`.

## Correcciones de la Entrega 1

| Observación que nos dieron | Qué cambiamos | Dónde se ve |
|---|---|---|
| Que el correo de los profesores también sirviera para registrarse | El registro acepta los dos dominios institucionales, definidos en una sola constante: `@uqvirtual.edu.co` (estudiantes) y `@uniquindio.edu.co` (profesores). Además, el correo se normaliza (sin espacios y en minúsculas) para que una persona no quede registrada dos veces | `domain/entity/UniCambista.java` (`DOMINIOS_INSTITUCIONALES` y `registrar`), commit `6cd6295`, prueba `UniCambistaTest.aceptaCorreoDeEstudianteYLoNormaliza` |

**Mejoras adicionales que hicimos por nuestra cuenta** (no fueron observaciones de la docente):

| Qué encontramos | Qué cambiamos | Dónde se ve |
|---|---|---|
| Se podía completar una Transacción sin EncuentroSeguro | `confirmar` exige el EncuentroSeguro, y solo el comprador o el vendedor pueden acordarlo | `Transaccion.java`, rama `feature/invariantes-dominio` |
| La regla de expiración a los 60 días solo estaba en el documento | Quedó programada en el dominio y expuesta como ruta de ADMIN | `Publicacion.expirarSiCorresponde`, `ExpirarPublicacionesUseCase` |
| La regla "no editar en proceso de compra" no estaba en código | Nuevo método `editar` que solo funciona si la publicación está ACTIVA | `Publicacion.editar` |
| Una regla de negocio rota respondía 400 | Ahora responde 422; el 400 queda para datos inválidos | `ManejadorGlobalDeExcepciones`, rama `feature/consultas-ranking-eliminacion` |
| El correo del usuario viajaba en el body | Ahora sale del token JWT | Controllers de `infrastructure/rest`, commit `f3f0de3` |

## Documentación

- `docs/entrega1-modelado-dominio.md`: modelado del dominio (Entrega 1).
- `docs/agregados-unisegundamano.md`: agregados, raíces e invariantes, con diagrama.
- `docs/persistencia-h2.md`: cómo se guardan los agregados con JPA.
- `glosario-lenguaje-obicuo.md`: lenguaje ubicuo.

## Pruebas avanzadas

### 1. Endpoint con seguridad
Elegimos la ruta `POST /api/admin/publicaciones/expirar`, que solo puede usar el rol ADMIN.
Hicimos tres pruebas: sin token responde 401, porque el sistema no sabe quién hace la petición;
con token pero con rol UNICAMBISTA responde 403, porque sabe quién es pero ese rol no tiene permiso;
y con rol ADMIN responde 200. Para simular el token usamos `jwt()` de `spring-security-test`,
que crea un usuario autenticado con los roles que le indiquemos sin tener que hacer login.
Usamos `@SpringBootTest` con `@AutoConfigureMockMvc` y no `@WebMvcTest`, porque `@WebMvcTest`
no carga nuestro `SecurityConfig` y estaríamos probando una seguridad por defecto, no la nuestra.
Lo que más nos costó fue entender que los roles van sin el prefijo `ROLE_`, porque así los
configuramos en el `JwtGrantedAuthoritiesConverter`.

### 2. @DataJpaTest contra @SpringBootTest
`@SpringBootTest` levanta toda la aplicación (controllers, seguridad, casos de uso y base de datos),
mientras que `@DataJpaTest` solo levanta la capa de persistencia: las entidades y las interfaces
`JpaRepository`. Por eso `@DataJpaTest` arranca más rápido, pero no carga nuestras propias clases:
tuvimos que traer el adaptador `UniCambistaRepositoryJpa` y su mapper con `@Import`, o la prueba
fallaba porque no encontraba el bean. Además, `@DataJpaTest` deshace cada prueba al terminar
sin necesidad de poner `@Transactional`. En Spring Boot 4 la anotación cambió de paquete a
`org.springframework.boot.data.jpa.test.autoconfigure` y necesita su propio starter,
`spring-boot-starter-data-jpa-test`, que agregamos en el `build.gradle`.

### 3. Prueba de integración
Nuestra prueba de integración hace una oferta por HTTP y recorre todo el sistema real:
seguridad, controller, caso de uso, dominio (con la regla del 50%), repositorio JPA y la base de
datos H2, sin ningún mock. Al final comprobamos la respuesta y además que la oferta sí quedó
guardada en la base de datos. A diferencia de una prueba unitaria, que prueba una sola clase y
simula todo lo demás, esta prueba detecta errores que aparecen cuando las piezas se juntan,
como un mapper mal hecho, una columna mal nombrada o una ruta equivocada. La desventaja es que
es más lenta y depende de los datos de `data.sql`. Por eso conviene hacer pocas, solo para los
flujos más importantes, y dejar los casos de borde a las pruebas unitarias.