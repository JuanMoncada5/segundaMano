package com.uniquindio.segundaMano.application.dto;

import java.math.BigDecimal;

/** Lo que llega para buscar Publicaciones. Los filtros en null significan "sin ese filtro". */
public record BuscarPublicacionesRequest(
        String texto,
        String curso,
        BigDecimal valorMin,
        BigDecimal valorMax,
        int pagina,
        int tamano
) {
}