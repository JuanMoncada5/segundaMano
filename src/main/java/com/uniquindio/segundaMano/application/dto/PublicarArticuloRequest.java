package com.uniquindio.segundaMano.application.dto;

import java.math.BigDecimal;

/**
 * Datos para el caso de uso PublicarArticulo.
 * curso y semestre son opcionales: si vienen los dos, se asocia una VigenciaAcademica.
 */
public record PublicarArticuloRequest(
        String correoDueno,
        String titulo,
        BigDecimal valor,
        String curso,
        String semestre
) {
    /** Para artículos sin VigenciaAcademica (laptops, muebles, etc.). */
    public PublicarArticuloRequest(String correoDueno, String titulo, BigDecimal valor) {
        this(correoDueno, titulo, valor, null, null);
    }

    public boolean tieneVigenciaAcademica() {
        return curso != null && !curso.isBlank() && semestre != null && !semestre.isBlank();
    }
}