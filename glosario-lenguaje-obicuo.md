# Glosario del Lenguaje Ubicuo - UniSegundaMano

## Conceptos Centrales

### UniCambista
*Definición:* Persona registrada en la plataforma con correo institucional verificado, que puede actuar tanto comprando como vendiendo artículos.

*Sinónimos aceptados:* Ninguno
*No usar:* Usuario, Cliente, Comprador/Vendedor (como roles separados), Miembro

*Ejemplo de uso en código:*
java
UniCambista uniCambista = UniCambista.registrar(
nombre, correoInstitucional, programaAcademico);


---

### Trueque
*Definición:* Modalidad en la que un UniCambista busca intercambiar un artículo por otro, sin que medie dinero.

*No usar:* Intercambio, Canje

*Ejemplo de uso en código:*
java
Trueque trueque = Trueque.proponer(articuloOfrecido, articuloSolicitado, unicambista);


---

### EncuentroSeguro
*Definición:* Punto físico designado dentro del Campus donde los UniCambistas coordinan la entrega de un artículo.

*Valores:* Biblioteca, Canchas, Edificios, Porterías, Cafeterías

*No usar:* Punto de encuentro, Lugar de entrega, Sitio de intercambio

*Precondiciones:*
- Debe corresponder a una ubicación predefinida dentro del Campus, no una dirección externa

*Ejemplo de uso en código:*
java
encuentroSeguro.confirmarUbicacion(transaccion);


---

### RachaConfianza
*Definición:* Indicador acumulado de reputación de un UniCambista, calculado a partir de las calificaciones de sus Transacciones.

*Precondiciones:*
- Un UniCambista solo puede calificar Transacciones en las que participó como comprador o vendedor
- Si cae por debajo de 3.0 estrellas después de 5 o más Transacciones, se muestra una advertencia visible

*En código:* RachaConfianza es el promedio *más* el total de transacciones evaluadas — ambos viajan juntos porque la advertencia depende de los dos a la vez.

*Ejemplo de uso en código:*
java
public class RachaConfianza {
private double promedioEstrellas;
private int totalTransacciones;

    public boolean debeMostrarAdvertencia() {
        return totalTransacciones >= 5 && promedioEstrellas < 3.0;
    }
}


---

### VigenciaAcadémica
*Definición:* Atributo opcional de una Publicación que indica el semestre o curso al que está ligado un artículo. Solo aplica a artículos cuya utilidad depende del calendario académico (libros, notas, calculadoras de una materia específica); artículos de uso general (laptops, muebles, electrónica) no la tienen. Su único propósito es dar visibilidad/contexto en el catálogo (ej. mostrar "Cálculo I - 2026-1" en la publicación); no afecta las reglas de expiración.

*No usar:* Fecha de vencimiento, Periodo de validez

*Precondiciones:*
- Es opcional: puede ser nula si el artículo no está ligado a un curso o semestre
- No modifica ni acelera la expiración de la Publicación — la regla de 60 días sin actividad aplica igual para todas, tengan o no VigenciaAcadémica

*Ejemplo de uso en código:*
java
public class Publicacion {
private VigenciaAcademica vigenciaAcademica; // puede ser null, solo informativa

    public String obtenerEtiquetaVisibilidad() {
        return (vigenciaAcademica != null)
            ? vigenciaAcademica.toString() // ej. "Cálculo I - 2026-1"
            : null;
    }
}


---

## Anti-patrones (Términos a EVITAR)

| No usar | Usar |
|---|---|
| "Usuario" / "Cliente" | "UniCambista" |
| "Intercambio" / "Canje" | "Trueque" |
| "Punto de encuentro" | "EncuentroSeguro" |
| "Rating" / "Score" | "RachaConfianza" |
| "Fecha de vencimiento" | "VigenciaAcadémica" |

---

## Reglas de Negocio Clave

1. *Un UniCambista no puede crear Publicaciones sin correo verificado*
2. *Una Publicación con Transacción activa se elimina lógicamente, no físicamente*
3. *Solo se puede calificar Transacciones en las que se participó*
4. *La contraoferta no puede ser menor al 50% del precio original*
5. *Una Publicación expira a los 60 días sin actividad, tenga o no VigenciaAcadémica*
6. *La RachaConfianza muestra advertencia si baja de 3.0 estrellas tras 5+ Transacciones*

---

## Uso en Código

*Buenas prácticas:*
java
// Correcto: Usa el lenguaje ubicuo
trueque.proponer(articuloOfrecido, articuloSolicitado, unicambista);
encuentroSeguro.confirmarUbicacion(transaccion);
publicacion.obtenerEtiquetaVisibilidad();
rachaConfianza.debeMostrarAdvertencia();

// Incorrecto: Términos técnicos genéricos
item.setExpirationDate(date);
transaction.setLocation(place);
user.getReputationScore();