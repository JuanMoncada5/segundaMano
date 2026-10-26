package com.uniquindio.segundaMano.application.usecase;

import com.uniquindio.segundaMano.application.dto.TransaccionResponse;
import com.uniquindio.segundaMano.domain.entity.UniCambista;
import com.uniquindio.segundaMano.domain.exception.RecursoNoEncontradoException;
import com.uniquindio.segundaMano.domain.repository.TransaccionRepository;
import com.uniquindio.segundaMano.domain.repository.UniCambistaRepository;

import java.util.List;

public class ListarTransaccionesDeUniCambistaUseCase {

    private final TransaccionRepository transaccionRepository;
    private final UniCambistaRepository uniCambistaRepository;

    public ListarTransaccionesDeUniCambistaUseCase(TransaccionRepository transaccionRepository,
                                                   UniCambistaRepository uniCambistaRepository) {
        this.transaccionRepository = transaccionRepository;
        this.uniCambistaRepository = uniCambistaRepository;
    }

    public List<TransaccionResponse> ejecutar(String correoInstitucional) {
        UniCambista uniCambista = uniCambistaRepository.buscarPorCorreo(correoInstitucional)
                .orElseThrow(() -> new RecursoNoEncontradoException("El UniCambista no está registrado"));
        return transaccionRepository.listarPorParticipante(uniCambista).stream()
                .map(TransaccionResponse::desde)
                .toList();
    }
}