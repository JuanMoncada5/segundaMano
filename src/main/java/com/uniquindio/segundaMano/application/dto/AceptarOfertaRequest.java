package com.uniquindio.segundaMano.application.dto;

public record AceptarOfertaRequest(
        String codigoPublicacion,
        String idOferta,
        String correoDueno
) {
}