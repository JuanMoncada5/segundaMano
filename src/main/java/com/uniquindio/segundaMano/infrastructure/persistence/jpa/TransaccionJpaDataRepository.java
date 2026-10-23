package com.uniquindio.segundaMano.infrastructure.persistence.jpa;

import com.uniquindio.segundaMano.infrastructure.persistence.jpa.entity.TransaccionEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface TransaccionJpaDataRepository extends JpaRepository<TransaccionEntity, String> {

    /** WHERE comprador_correo = ? OR vendedor_correo = ? */
    List<TransaccionEntity> findByCompradorCorreoOrVendedorCorreo(String compradorCorreo, String vendedorCorreo);
}