package com.uniquindio.segundaMano.infrastructure.rest.exception;

import com.uniquindio.segundaMano.domain.exception.RecursoNoEncontradoException;
import com.uniquindio.segundaMano.domain.exception.ReglaDominioException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.List;

/** Traduce las excepciones a respuestas HTTP con un mismo formato (ErrorApi). */
@RestControllerAdvice
public class ManejadorGlobalDeExcepciones {

    private static final Logger LOG = LoggerFactory.getLogger(ManejadorGlobalDeExcepciones.class);

    /** Una regla del dominio se rompió -> 400. */
    @ExceptionHandler(ReglaDominioException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorApi manejarReglaDominio(ReglaDominioException excepcion) {
        return ErrorApi.de(400, "Regla de dominio", excepcion.getMessage());
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

    /** Cualquier otro error inesperado -> 500, sin exponer detalles internos. */
    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ErrorApi manejarInesperado(Exception excepcion) {
        LOG.error("Error inesperado", excepcion);
        return ErrorApi.de(500, "Error interno", "Ocurrió un error inesperado");
    }
}
