package com.uniquindio.segundaMano.application.dto;

/**
 * zona: texto con el nombre de una ZonaCampus (BIBLIOTECA, CANCHAS, EDIFICIOS, PORTERIAS, CAFETERIAS).
 * referencia: detalle opcional dentro de la zona (ej. "Central", "Bloque A").
 */
public record AsignarEncuentroSeguroRequest(
        String idTransaccion,
        String correoUniCambista,
        String zona,
        String referencia
) {
}