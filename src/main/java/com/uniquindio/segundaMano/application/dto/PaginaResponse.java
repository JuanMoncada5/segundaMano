package com.uniquindio.segundaMano.application.dto;

import java.util.List;

/** Respuesta paginada: el contenido de la página, el total de elementos y el total de páginas. */
public record PaginaResponse<T>(
        List<T> contenido,
        long totalElementos,
        int totalPaginas,
        int pagina,
        int tamano
) {
}