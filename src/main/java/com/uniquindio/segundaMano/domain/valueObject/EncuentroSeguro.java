package com.uniquindio.segundaMano.domain.valueObject;

import com.uniquindio.segundaMano.domain.exception.ReglaDominioException;

/**
 * Punto del Campus donde los UniCambistas coordinan la entrega.
 * zona: obligatoria y siempre una ZonaCampus predefinida.
 * referencia: detalle opcional dentro de la zona (ej. "Central", "Bloque A").
 */
public record EncuentroSeguro(ZonaCampus zona, String referencia) {

    public EncuentroSeguro {
        if (zona == null) {
            throw new ReglaDominioException("El EncuentroSeguro debe ser una zona predefinida del Campus");
        }
        referencia = (referencia == null || referencia.isBlank()) ? null : referencia.trim();
    }

    public EncuentroSeguro(ZonaCampus zona) {
        this(zona, null);
    }
}

