package com.uniquindio.segundaMano.application.usecase;

import com.uniquindio.segundaMano.application.dto.PublicacionResponse;
import com.uniquindio.segundaMano.application.dto.PublicarArticuloRequest;
import com.uniquindio.segundaMano.domain.entity.Publicacion;
import com.uniquindio.segundaMano.domain.entity.UniCambista;
import com.uniquindio.segundaMano.domain.exception.RecursoNoEncontradoException;
import com.uniquindio.segundaMano.domain.exception.ReglaDominioException;
import com.uniquindio.segundaMano.domain.repository.PublicacionRepository;
import com.uniquindio.segundaMano.domain.repository.UniCambistaRepository;
import com.uniquindio.segundaMano.domain.valueObject.VigenciaAcademica;

public class PublicarArticuloUseCase {

    private final PublicacionRepository publicacionRepository;
    private final UniCambistaRepository uniCambistaRepository;

    public PublicarArticuloUseCase(PublicacionRepository publicacionRepository,
                                   UniCambistaRepository uniCambistaRepository) {
        this.publicacionRepository = publicacionRepository;
        this.uniCambistaRepository = uniCambistaRepository;
    }

    public PublicacionResponse ejecutar(PublicarArticuloRequest request) {
        UniCambista dueno = uniCambistaRepository.buscarPorCorreo(request.correoDueno())
                .orElseThrow(() -> new RecursoNoEncontradoException("El UniCambista no está registrado"));
        Publicacion publicacion = Publicacion.publicar(dueno, request.titulo(), request.valor());
        if (request.tieneVigenciaAcademica()) {
            publicacion.asociarVigenciaAcademica(new VigenciaAcademica(request.curso(), request.semestre()));
        }
        publicacionRepository.guardar(publicacion);
        return PublicacionResponse.desde(publicacion);
    }
}