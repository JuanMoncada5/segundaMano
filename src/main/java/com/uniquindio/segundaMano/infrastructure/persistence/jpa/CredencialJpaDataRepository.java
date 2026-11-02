package com.uniquindio.segundaMano.infrastructure.persistence.jpa;

import com.uniquindio.segundaMano.infrastructure.persistence.jpa.entity.CredencialEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CredencialJpaDataRepository extends JpaRepository<CredencialEntity, String> {
}
