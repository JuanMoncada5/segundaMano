package com.uniquindio.segundaMano.application.dto;

import com.uniquindio.segundaMano.domain.entity.Publicacion;

import java.math.BigDecimal;

/** Una fila del ranking: los datos salen de la Publicación real, nada quemado. */
public record PublicacionRankingResponse(
        int posicion,
        String codigo,
        String titulo,
        BigDecimal valor,
        String correoDueno,
        int cantidadOfertas
) {
    public static PublicacionRankingResponse desde(int posicion, Publicacion publicacion) {
        return new PublicacionRankingResponse(
                posicion,
                publicacion.getCodigo(),
                publicacion.getTitulo(),
                publicacion.getValor(),
                publicacion.getDueno().getCorreoInstitucional(),
                publicacion.getOfertas().size()
        );
    }
}