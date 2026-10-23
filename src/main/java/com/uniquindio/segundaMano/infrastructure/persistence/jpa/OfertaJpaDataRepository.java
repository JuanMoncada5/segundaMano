package com.uniquindio.segundaMano.infrastructure.persistence.jpa;

import com.uniquindio.segundaMano.infrastructure.persistence.jpa.entity.OfertaEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

/** Solo lo usa PublicacionRepositoryJpa: las Ofertas se guardan y se leen a través de su Publicacion. */
public interface OfertaJpaDataRepository extends JpaRepository<OfertaEntity, String> {

    /** WHERE publicacion_codigo = ? */
    List<OfertaEntity> findByPublicacionCodigo(String publicacionCodigo);
}