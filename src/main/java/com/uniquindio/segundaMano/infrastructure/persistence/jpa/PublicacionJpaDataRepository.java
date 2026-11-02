package com.uniquindio.segundaMano.infrastructure.persistence.jpa;

import com.uniquindio.segundaMano.domain.valueObject.EstadoPublicacion;
import com.uniquindio.segundaMano.infrastructure.persistence.jpa.entity.PublicacionEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.math.BigDecimal;
import java.util.List;

public interface PublicacionJpaDataRepository extends JpaRepository<PublicacionEntity, String> {

    /** WHERE estado = ? (la base de datos filtra, no Java). */
    List<PublicacionEntity> findByEstado(EstadoPublicacion estado);

    /**
     * Ranking: une publicaciones con ofertas (JOIN), cuenta las ofertas de cada una (GROUP BY)
     * y ordena de mayor a menor. Devuelve solo los códigos; el Pageable limita cuántos.
     */
    @Query("""
            SELECT p.codigo
            FROM PublicacionEntity p
            JOIN OfertaEntity o ON o.publicacionCodigo = p.codigo
            WHERE p.estado = :estado
            GROUP BY p.codigo
            ORDER BY COUNT(o) DESC, p.codigo ASC
            """)
    List<String> rankingCodigosPorCantidadDeOfertas(@Param("estado") EstadoPublicacion estado, Pageable pageable);

    /**
     * Búsqueda paginada con JPQL y parámetros con nombre.
     * - Solo el estado que se pida (ACTIVA): las ELIMINADAS nunca salen.
     * - texto: busca dentro del título sin importar mayúsculas ('' = sin filtro).
     * - curso, valorMin, valorMax: si llegan en null, ese filtro no se aplica.
     * Spring arma solo la consulta de conteo (total de elementos) y aplica el Pageable (página, tamaño, orden).
     */
    @Query("""
            SELECT p
            FROM PublicacionEntity p
            WHERE p.estado = :estado
              AND LOWER(p.titulo) LIKE LOWER(CONCAT('%', :texto, '%'))
              AND (:curso IS NULL OR LOWER(p.vigenciaAcademica.curso) = LOWER(:curso))
              AND (:valorMin IS NULL OR p.valor >= :valorMin)
              AND (:valorMax IS NULL OR p.valor <= :valorMax)
            """)
    Page<PublicacionEntity> buscar(@Param("estado") EstadoPublicacion estado,
                                   @Param("texto") String texto,
                                   @Param("curso") String curso,
                                   @Param("valorMin") BigDecimal valorMin,
                                   @Param("valorMax") BigDecimal valorMax,
                                   Pageable pageable);
}