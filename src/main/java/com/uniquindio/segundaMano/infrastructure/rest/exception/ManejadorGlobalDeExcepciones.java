package com.uniquindio.segundaMano.infrastructure.rest.exception;

import com.uniquindio.segundaMano.domain.exception.RecursoNoEncontradoException;
import com.uniquindio.segundaMano.domain.exception.ReglaDominioException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.servlet.resource.NoResourceFoundException;

import java.util.List;

/** Traduce las excepciones a respuestas HTTP con un mismo formato (ErrorApi). */
@RestControllerAdvice
public class ManejadorGlobalDeExcepciones {

    private static final Logger LOG = LoggerFactory.getLogger(ManejadorGlobalDeExcepciones.class);

    /**
     * Una regla del dominio se rompió -> 422 (Unprocessable Content): la petición está bien formada,
     * pero el negocio no permite hacer eso (ej. ofertar menos del 50%, eliminar algo que no es tuyo).
     * El 400 queda solo para datos mal enviados.
     */
    @ExceptionHandler(ReglaDominioException.class)
    public ResponseEntity<ErrorApi> manejarReglaDominio(ReglaDominioException excepcion) {
        return ResponseEntity.status(422).body(ErrorApi.de(422, "Regla de negocio", excepcion.getMessage()));
    }

    /** Lo que se buscaba no existe -> 404 (es más específica que la anterior, Spring la prefiere). */
    @ExceptionHandler(RecursoNoEncontradoException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ErrorApi manejarNoEncontrado(RecursoNoEncontradoException excepcion) {
        return ErrorApi.de(404, "No encontrado", excepcion.getMessage());
    }

    /** Falló @Valid en un body -> 400 con el detalle campo por campo. */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorApi manejarValidacion(MethodArgumentNotValidException excepcion) {
        List<String> detalles = excepcion.getBindingResult().getFieldErrors().stream()
                .map(error -> error.getDefaultMessage())
                .toList();
        return new ErrorApi(400, "Validación", "La petición tiene campos inválidos", detalles);
    }

    /** JSON mal formado o con tipos incorrectos -> 400. */
    @ExceptionHandler(HttpMessageNotReadableException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorApi manejarJsonInvalido(HttpMessageNotReadableException excepcion) {
        return ErrorApi.de(400, "JSON inválido", "El cuerpo de la petición no se pudo leer");
    }

    /** Entrada inválida validada en un caso de uso (ej. página negativa, límite fuera de rango) -> 400. */
    @ExceptionHandler(IllegalArgumentException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorApi manejarEntradaInvalida(IllegalArgumentException excepcion) {
        return ErrorApi.de(400, "Datos inválidos", excepcion.getMessage());
    }

    /** Falta un parámetro obligatorio en la URL -> 400. */
    @ExceptionHandler(MissingServletRequestParameterException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorApi manejarParametroFaltante(MissingServletRequestParameterException excepcion) {
        return ErrorApi.de(400, "Parámetro faltante", "Falta el parámetro '" + excepcion.getParameterName() + "'");
    }

    /** Un parámetro con el tipo equivocado (ej. pagina=abc) -> 400. */
    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorApi manejarTipoIncorrecto(MethodArgumentTypeMismatchException excepcion) {
        return ErrorApi.de(400, "Parámetro inválido", "El parámetro '" + excepcion.getName() + "' tiene un valor inválido");
    }

    /** La ruta no existe -> 404 (antes caía en el 500 de abajo). */
    @ExceptionHandler(NoResourceFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ErrorApi manejarRutaInexistente(NoResourceFoundException excepcion) {
        return ErrorApi.de(404, "No encontrado", "La ruta no existe");
    }

    /** Método HTTP equivocado (ej. GET donde va POST) -> 405. */
    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    @ResponseStatus(HttpStatus.METHOD_NOT_ALLOWED)
    public ErrorApi manejarMetodoNoPermitido(HttpRequestMethodNotSupportedException excepcion) {
        return ErrorApi.de(405, "Método no permitido", "Esta ruta no acepta " + excepcion.getMethod());
    }

    /** Cualquier otro error inesperado -> 500, sin exponer detalles internos. */
    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ErrorApi manejarInesperado(Exception excepcion) {
        LOG.error("Error inesperado", excepcion);
        return ErrorApi.de(500, "Error interno", "Ocurrió un error inesperado");
    }
}