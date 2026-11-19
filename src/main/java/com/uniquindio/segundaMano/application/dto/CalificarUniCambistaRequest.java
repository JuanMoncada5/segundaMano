package com.uniquindio.segundaMano.application.dto;

/**
 * Datos para calificar al otro participante de una Transacción.
 * correoCalificador sale del token (quién está autenticado), nunca del body.
 */
public record CalificarUniCambistaRequest(
        String idTransaccion,
        String correoCalificador,
        int estrellas
) {
}