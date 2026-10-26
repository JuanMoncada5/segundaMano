package com.uniquindio.segundaMano.infrastructure.rest.exception;

import java.util.List;

/** Forma única de TODOS los errores que devuelve la API. */
public record ErrorApi(int estado, String error, String mensaje, List<String> detalles) {

    public static ErrorApi de(int estado, String error, String mensaje) {
        return new ErrorApi(estado, error, mensaje, List.of());
    }
}
