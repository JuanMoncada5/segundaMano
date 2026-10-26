package com.uniquindio.segundaMano.application.usecase;

import com.uniquindio.segundaMano.application.dto.TransaccionResponse;
import com.uniquindio.segundaMano.domain.exception.RecursoNoEncontradoException;
import com.uniquindio.segundaMano.domain.repository.TransaccionRepository;

public class ConsultarTransaccionUseCase {

    private final TransaccionRepository transaccionRepository;

    public ConsultarTransaccionUseCase(TransaccionRepository transaccionRepository) {
        this.transaccionRepository = transaccionRepository;
    }

    public TransaccionResponse ejecutar(String idTransaccion) {
        return transaccionRepository.buscarPorId(idTransaccion)
                .map(TransaccionResponse::desde)
                .orElseThrow(() -> new RecursoNoEncontradoException("La Transacción no existe"));
    }
}