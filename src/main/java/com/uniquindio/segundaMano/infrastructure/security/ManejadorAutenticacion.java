package com.uniquindio.segundaMano.infrastructure.security;

import com.uniquindio.segundaMano.application.auth.CredencialesInvalidasException;
import com.uniquindio.segundaMano.infrastructure.rest.exception.ErrorApi;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

/**
 * Login fallido -> 401. Está en su propia clase con prioridad máxima para que el manejador genérico
 * (Exception -> 500) del ManejadorGlobalDeExcepciones no lo atienda primero.
 */
@RestControllerAdvice
@Order(Ordered.HIGHEST_PRECEDENCE)
public class ManejadorAutenticacion {

    @ExceptionHandler(CredencialesInvalidasException.class)
    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    public ErrorApi manejarCredencialesInvalidas(CredencialesInvalidasException excepcion) {
        return ErrorApi.de(401, "Credenciales inválidas", excepcion.getMessage());
    }
}
