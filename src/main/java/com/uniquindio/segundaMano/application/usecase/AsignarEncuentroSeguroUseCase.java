package com.uniquindio.segundaMano.application.usecase;

import com.uniquindio.segundaMano.application.dto.AsignarEncuentroSeguroRequest;
import com.uniquindio.segundaMano.application.dto.TransaccionResponse;
import com.uniquindio.segundaMano.domain.entity.Transaccion;
import com.uniquindio.segundaMano.domain.entity.UniCambista;
import com.uniquindio.segundaMano.domain.exception.RecursoNoEncontradoException;
import com.uniquindio.segundaMano.domain.repository.TransaccionRepository;
import com.uniquindio.segundaMano.domain.repository.UniCambistaRepository;
import com.uniquindio.segundaMano.domain.valueObject.EncuentroSeguro;
import com.uniquindio.segundaMano.domain.valueObject.ZonaCampus;

public class AsignarEncuentroSeguroUseCase {

    private final TransaccionRepository transaccionRepository;
    private final UniCambistaRepository uniCambistaRepository;

    public AsignarEncuentroSeguroUseCase(TransaccionRepository transaccionRepository,
                                         UniCambistaRepository uniCambistaRepository) {
        this.transaccionRepository = transaccionRepository;
        this.uniCambistaRepository = uniCambistaRepository;
    }

    public TransaccionResponse ejecutar(AsignarEncuentroSeguroRequest request) {
        Transaccion transaccion = transaccionRepository.buscarPorId(request.idTransaccion())
                .orElseThrow(() -> new RecursoNoEncontradoException("La Transacción no existe"));
        UniCambista quienAsigna = uniCambistaRepository.buscarPorCorreo(request.correoUniCambista())
                .orElseThrow(() -> new RecursoNoEncontradoException("El UniCambista no está registrado"));
        EncuentroSeguro encuentro = new EncuentroSeguro(ZonaCampus.desde(request.zona()), request.referencia());

        transaccion.asignarEncuentroSeguro(quienAsigna, encuentro);
        transaccionRepository.guardar(transaccion);
        return TransaccionResponse.desde(transaccion);
    }
}