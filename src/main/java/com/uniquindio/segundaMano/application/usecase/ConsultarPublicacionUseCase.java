package com.uniquindio.segundaMano.application.usecase;

import com.uniquindio.segundaMano.application.dto.PublicacionResponse;
import com.uniquindio.segundaMano.domain.exception.RecursoNoEncontradoException;
import com.uniquindio.segundaMano.domain.repository.PublicacionRepository;

public class ConsultarPublicacionUseCase {

    private final PublicacionRepository publicacionRepository;

    public ConsultarPublicacionUseCase(PublicacionRepository publicacionRepository) {
        this.publicacionRepository = publicacionRepository;
    }

    public PublicacionResponse ejecutar(String codigo) {
        return publicacionRepository.buscarPorCodigo(codigo)
                .map(PublicacionResponse::desde)
                .orElseThrow(() -> new RecursoNoEncontradoException("La Publicación no existe"));
    }
}
