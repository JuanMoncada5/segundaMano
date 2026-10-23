package com.uniquindio.segundaMano.infrastructure.persistence.jpa;

import com.uniquindio.segundaMano.domain.valueObject.EstadoPublicacion;
import com.uniquindio.segundaMano.infrastructure.persistence.jpa.entity.PublicacionEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PublicacionJpaDataRepository extends JpaRepository<PublicacionEntity, String> {

    /** WHERE estado = ? (la base de datos filtra, no Java). */
    List<PublicacionEntity> findByEstado(EstadoPublicacion estado);
}
