package com.uniquindio.segundaMano.application.dto;

import com.uniquindio.segundaMano.domain.entity.Oferta;

import java.math.BigDecimal;

public record OfertaResponse(
        String id,
        String codigoPublicacion,
        String correoOferente,
        BigDecimal montoPropuesto,
        String estado
) {
    public static OfertaResponse desde(Oferta oferta) {
        return new OfertaResponse(
                oferta.getId(),
                oferta.getPublicacion().getCodigo(),
                oferta.getOferente().getCorreoInstitucional(),
                oferta.getMontoPropuesto(),
                oferta.getEstado().name()
        );
    }
}