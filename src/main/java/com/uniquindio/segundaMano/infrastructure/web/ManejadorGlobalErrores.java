package com.uniquindio.segundaMano.infrastructure.web;

import com.uniquindio.segundaMano.domain.exception.RecursoNoEncontradoException;
import com.uniquindio.segundaMano.domain.exception.ReglaDominioException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

/**
 * Convierte las excepciones del dominio en respuestas HTTP:
 *  - RecursoNoEncontradoException -> 404 Not Found
 *  - ReglaDominioException        -> 400 Bad Request (se rompió una regla de negocio)
 * Como RecursoNoEncontradoException hereda de ReglaDominioException, Spring elige
 * el manejador más específico: por eso un "no existe" responde 404 y no 400.
 */
@RestControllerAdvice
public class ManejadorGlobalErrores {

    public record ErrorResponse(int status, String error, String mensaje) {
    }

    @ExceptionHandler(RecursoNoEncontradoException.class)
    public ResponseEntity<ErrorResponse> noEncontrado(RecursoNoEncontradoException e) {
        return responder(HttpStatus.NOT_FOUND, e.getMessage());
    }

    @ExceptionHandler(ReglaDominioException.class)
    public ResponseEntity<ErrorResponse> reglaRota(ReglaDominioException e) {
        return responder(HttpStatus.BAD_REQUEST, e.getMessage());
    }

    private ResponseEntity<ErrorResponse> responder(HttpStatus status, String mensaje) {
        return ResponseEntity.status(status)
                .body(new ErrorResponse(status.value(), status.getReasonPhrase(), mensaje));
    }
}