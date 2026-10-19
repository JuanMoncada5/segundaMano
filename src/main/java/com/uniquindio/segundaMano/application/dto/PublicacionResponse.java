package com.uniquindio.segundaMano.application.dto;

import com.uniquindio.segundaMano.domain.entity.Publicacion;

import java.math.BigDecimal;

public record PublicacionResponse(
        String codigo,
        String correoDueno,
        String titulo,
        BigDecimal valor,
        String estado,
        String etiquetaVigenciaAcademica
) {
    public static PublicacionResponse desde(Publicacion publicacion) {
        return new PublicacionResponse(
                publicacion.getCodigo(),
                publicacion.getDueno().getCorreoInstitucional(),
                publicacion.getTitulo(),
                publicacion.getValor(),
                publicacion.getEstado().name(),
                publicacion.obtenerEtiquetaVisibilidad()
        );
    }
}
