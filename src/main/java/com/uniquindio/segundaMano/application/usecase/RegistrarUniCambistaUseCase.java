package com.uniquindio.segundaMano.application.usecase;

import com.uniquindio.segundaMano.application.dto.RegistrarUniCambistaRequest;
import com.uniquindio.segundaMano.application.dto.UniCambistaResponse;
import com.uniquindio.segundaMano.domain.entity.UniCambista;
import com.uniquindio.segundaMano.domain.exception.ReglaDominioException;
import com.uniquindio.segundaMano.domain.repository.UniCambistaRepository;

public class RegistrarUniCambistaUseCase {

    private final UniCambistaRepository uniCambistaRepository;

    public RegistrarUniCambistaUseCase(UniCambistaRepository uniCambistaRepository) {
        this.uniCambistaRepository = uniCambistaRepository;
    }

    public UniCambistaResponse ejecutar(RegistrarUniCambistaRequest request) {
        UniCambista uniCambista = UniCambista.registrar(request.correoInstitucional(), request.nombre());
        if (uniCambistaRepository.buscarPorCorreo(uniCambista.getCorreoInstitucional()).isPresent()) {
            throw new ReglaDominioException("Ya existe un UniCambista registrado con ese correo");
        }
        uniCambistaRepository.guardar(uniCambista);
        return UniCambistaResponse.desde(uniCambista);
    }
}