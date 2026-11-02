package com.uniquindio.segundaMano.domain.repository;

import java.util.List;

/**
 * Una página de resultados, sin depender de la clase Page de Spring.
 * El adaptador JPA convierte el Page de Spring a este record.
 */
public record Pagina<T>(
        List<T> contenido,
        long totalElementos,
        int totalPaginas,
        int pagina,
        int tamano
) {
}