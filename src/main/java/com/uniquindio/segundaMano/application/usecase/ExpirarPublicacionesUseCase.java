package com.uniquindio.segundaMano.application.usecase;

import com.uniquindio.segundaMano.domain.entity.Publicacion;
import com.uniquindio.segundaMano.domain.repository.PublicacionRepository;

import java.time.LocalDate;

/**
 * Recorre las Publicaciones activas y expira las que llevan 60 días o más sin actividad.
 * La regla vive en Publicacion.expirarSiCorresponde; aquí solo se orquesta.
 */
public class ExpirarPublicacionesUseCase {

    private final PublicacionRepository publicacionRepository;

    public ExpirarPublicacionesUseCase(PublicacionRepository publicacionRepository) {
        this.publicacionRepository = publicacionRepository;
    }

    /** @return cuántas Publicaciones expiraron */
    public int ejecutar(LocalDate hoy) {
        int expiradas = 0;
        for (Publicacion publicacion : publicacionRepository.listarActivas()) {
            if (publicacion.expirarSiCorresponde(hoy)) {
                publicacionRepository.guardar(publicacion);
                expiradas++;
            }
        }
        return expiradas;
    }
}