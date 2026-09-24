package com.uniquindio.segundaMano.application.usecase;

import com.uniquindio.segundaMano.domain.entity.Publicacion;
import com.uniquindio.segundaMano.domain.entity.UniCambista;
import com.uniquindio.segundaMano.domain.repository.PublicacionRepository;

import java.math.BigDecimal;

public class PublicarArticuloUseCase {

    private final PublicacionRepository publicacionRepository;

    public PublicarArticuloUseCase(PublicacionRepository publicacionRepository) {
        this.publicacionRepository = publicacionRepository;
    }

    public Publicacion ejecutar(UniCambista dueno, String titulo, BigDecimal valor) {
        Publicacion publicacion = Publicacion.publicar(dueno, titulo, valor);
        publicacionRepository.guardar(publicacion);
        return publicacion;
    }
}