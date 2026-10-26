# UniSegundaMano

Marketplace de segunda mano para estudiantes de la Universidad del Quindío.
Programación Avanzada · Juan Moncada y Juan Manuel Rincón

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