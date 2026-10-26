package com.uniquindio.segundaMano.application.usecase;

import com.uniquindio.segundaMano.application.dto.PublicacionResponse;
import com.uniquindio.segundaMano.domain.repository.PublicacionRepository;

import java.util.List;

public class ListarPublicacionesActivasUseCase {

    private final PublicacionRepository publicacionRepository;

    public ListarPublicacionesActivasUseCase(PublicacionRepository publicacionRepository) {
        this.publicacionRepository = publicacionRepository;
    }

    public List<PublicacionResponse> ejecutar() {
        return publicacionRepository.listarActivas().stream()
                .map(PublicacionResponse::desde)
                .toList();
    }
}
