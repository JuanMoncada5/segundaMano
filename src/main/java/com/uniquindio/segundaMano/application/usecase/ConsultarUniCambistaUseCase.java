package com.uniquindio.segundaMano.application.usecase;

import com.uniquindio.segundaMano.application.dto.UniCambistaResponse;
import com.uniquindio.segundaMano.domain.exception.RecursoNoEncontradoException;
import com.uniquindio.segundaMano.domain.repository.UniCambistaRepository;

public class ConsultarUniCambistaUseCase {

    private final UniCambistaRepository uniCambistaRepository;

    public ConsultarUniCambistaUseCase(UniCambistaRepository uniCambistaRepository) {
        this.uniCambistaRepository = uniCambistaRepository;
    }

    public UniCambistaResponse ejecutar(String correoInstitucional) {
        return uniCambistaRepository.buscarPorCorreo(correoInstitucional)
                .map(UniCambistaResponse::desde)
                .orElseThrow(() -> new RecursoNoEncontradoException("El UniCambista no está registrado"));
    }
}
