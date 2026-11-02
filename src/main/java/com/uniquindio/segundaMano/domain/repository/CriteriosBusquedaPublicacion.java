package com.uniquindio.segundaMano.domain.repository;

import java.math.BigDecimal;

/**
 * Lo que se puede pedir al buscar Publicaciones. Es un tipo PROPIO del dominio:
 * así el puerto (PublicacionRepository) no depende de Pageable ni de nada de Spring.
 * texto, curso, valorMin y valorMax pueden venir en null (= "sin ese filtro").
 */
public record CriteriosBusquedaPublicacion(
        String texto,
        String curso,
        BigDecimal valorMin,
        BigDecimal valorMax,
        int pagina,
        int tamano
) {
}