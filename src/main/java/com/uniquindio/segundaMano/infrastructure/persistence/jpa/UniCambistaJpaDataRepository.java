package com.uniquindio.segundaMano.infrastructure.persistence.jpa;

import com.uniquindio.segundaMano.infrastructure.persistence.jpa.entity.UniCambistaEntity;
import org.springframework.data.jpa.repository.JpaRepository;

/** Interfaz vacía: Spring escribe guardar, buscar y listar. */
public interface UniCambistaJpaDataRepository extends JpaRepository<UniCambistaEntity, String> {
}
