package com.uniquindio.segundaMano.infrastructure.persistence.jpa;

import com.uniquindio.segundaMano.domain.valueObject.EstadoPublicacion;
import com.uniquindio.segundaMano.infrastructure.persistence.jpa.entity.PublicacionEntity;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

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
}