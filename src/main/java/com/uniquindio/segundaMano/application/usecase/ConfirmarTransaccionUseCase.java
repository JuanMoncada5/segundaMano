package com.uniquindio.segundaMano.application.usecase;

import com.uniquindio.segundaMano.application.dto.ConfirmarTransaccionRequest;
import com.uniquindio.segundaMano.application.dto.TransaccionResponse;
import com.uniquindio.segundaMano.domain.entity.Transaccion;
import com.uniquindio.segundaMano.domain.entity.UniCambista;
import com.uniquindio.segundaMano.domain.exception.RecursoNoEncontradoException;
import com.uniquindio.segundaMano.domain.exception.ReglaDominioException;
import com.uniquindio.segundaMano.domain.repository.TransaccionRepository;
import com.uniquindio.segundaMano.domain.repository.UniCambistaRepository;

public class ConfirmarTransaccionUseCase {

    private final TransaccionRepository transaccionRepository;
    private final UniCambistaRepository uniCambistaRepository;

    public ConfirmarTransaccionUseCase(TransaccionRepository transaccionRepository,
                                       UniCambistaRepository uniCambistaRepository) {
        this.transaccionRepository = transaccionRepository;
        this.uniCambistaRepository = uniCambistaRepository;
    }

    public TransaccionResponse ejecutar(ConfirmarTransaccionRequest request) {
        Transaccion transaccion = transaccionRepository.buscarPorId(request.idTransaccion())
                .orElseThrow(() -> new RecursoNoEncontradoException("La Transacción no existe"));
        UniCambista quienConfirma = uniCambistaRepository.buscarPorCorreo(request.correoUniCambista())
                .orElseThrow(() -> new RecursoNoEncontradoException("El UniCambista no está registrado"));

        transaccion.confirmar(quienConfirma);
        transaccionRepository.guardar(transaccion);
        return TransaccionResponse.desde(transaccion);
    }
}
