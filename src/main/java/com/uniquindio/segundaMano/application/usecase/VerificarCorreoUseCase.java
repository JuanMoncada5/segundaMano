package com.uniquindio.segundaMano.application.usecase;

import com.uniquindio.segundaMano.application.dto.UniCambistaResponse;
import com.uniquindio.segundaMano.domain.entity.UniCambista;
import com.uniquindio.segundaMano.domain.exception.RecursoNoEncontradoException;
import com.uniquindio.segundaMano.domain.repository.UniCambistaRepository;

/** Marca el correo del UniCambista como verificado (en un sistema real llegaría tras confirmar un enlace). */
public class VerificarCorreoUseCase {

    private final UniCambistaRepository uniCambistaRepository;

    public VerificarCorreoUseCase(UniCambistaRepository uniCambistaRepository) {
        this.uniCambistaRepository = uniCambistaRepository;
    }

    public UniCambistaResponse ejecutar(String correoInstitucional) {
        UniCambista uniCambista = uniCambistaRepository.buscarPorCorreo(correoInstitucional)
                .orElseThrow(() -> new RecursoNoEncontradoException("El UniCambista no está registrado"));
        uniCambista.verificarCorreo();
        uniCambistaRepository.guardar(uniCambista);
        return UniCambistaResponse.desde(uniCambista);
    }
}