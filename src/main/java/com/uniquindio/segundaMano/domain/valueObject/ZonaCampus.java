package com.uniquindio.segundaMano.domain.valueObject;

import com.uniquindio.segundaMano.domain.exception.ReglaDominioException;

/**
 * Zonas predefinidas del Campus donde se puede coordinar un EncuentroSeguro.
 * Al ser un enum, es imposible crear una zona que no exista en el Campus.
 */
public enum ZonaCampus {
    BIBLIOTECA,
    CANCHAS,
    EDIFICIOS,
    PORTERIAS,
    CAFETERIAS;

    /** Convierte el texto que llega en un DTO (ej. "biblioteca") en una zona del Campus. */
    public static ZonaCampus desde(String texto) {
        if (texto == null || texto.isBlank()) {
            throw new ReglaDominioException("Debe indicarse la zona del Campus");
        }
        try {
            return ZonaCampus.valueOf(texto.trim().toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new ReglaDominioException("La zona '" + texto + "' no es una zona predefinida del Campus");
        }
    }
}