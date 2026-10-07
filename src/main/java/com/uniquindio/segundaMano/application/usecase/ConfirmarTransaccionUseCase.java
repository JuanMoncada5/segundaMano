package com.uniquindio.segundaMano.application.usecase;

import com.uniquindio.segundaMano.domain.entity.Transaccion;
import com.uniquindio.segundaMano.domain.entity.UniCambista;
import com.uniquindio.segundaMano.domain.exception.ReglaDominioException;
import com.uniquindio.segundaMano.domain.repository.TransaccionRepository;

public class ConfirmarTransaccionUseCase {
    private final TransaccionRepository transaccionRepository;

    public ConfirmarTransaccionUseCase(TransaccionRepository transaccionRepository) {
        this.transaccionRepository = transaccionRepository;
    }

    public Transaccion ejecutar(String idTransaccion, UniCambista quienConfirma) {
        Transaccion transaccion = transaccionRepository.buscarPorId(idTransaccion)
                .orElseThrow(() -> new ReglaDominioException("La Transacción no existe"));
        transaccion.confirmar(quienConfirma);
        transaccionRepository.guardar(transaccion);
        return transaccion;
    }
}
