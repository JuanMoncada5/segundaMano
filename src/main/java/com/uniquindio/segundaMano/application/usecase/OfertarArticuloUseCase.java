package com.uniquindio.segundaMano.application.usecase;

import com.uniquindio.segundaMano.application.dto.OfertaResponse;
import com.uniquindio.segundaMano.application.dto.OfertarArticuloRequest;
import com.uniquindio.segundaMano.domain.entity.Oferta;
import com.uniquindio.segundaMano.domain.entity.Publicacion;
import com.uniquindio.segundaMano.domain.entity.UniCambista;
import com.uniquindio.segundaMano.domain.exception.RecursoNoEncontradoException;
import com.uniquindio.segundaMano.domain.exception.ReglaDominioException;
import com.uniquindio.segundaMano.domain.repository.PublicacionRepository;
import com.uniquindio.segundaMano.domain.repository.UniCambistaRepository;

public class OfertarArticuloUseCase {

    private final PublicacionRepository publicacionRepository;
    private final UniCambistaRepository uniCambistaRepository;

    public OfertarArticuloUseCase(PublicacionRepository publicacionRepository,
                                  UniCambistaRepository uniCambistaRepository) {
        this.publicacionRepository = publicacionRepository;
        this.uniCambistaRepository = uniCambistaRepository;
    }

    public OfertaResponse ejecutar(OfertarArticuloRequest request) {
        Publicacion publicacion = publicacionRepository.buscarPorCodigo(request.codigoPublicacion())
                .orElseThrow(() -> new RecursoNoEncontradoException("La Publicación no existe"));
        UniCambista oferente = uniCambistaRepository.buscarPorCorreo(request.correoOferente())
                .orElseThrow(() -> new RecursoNoEncontradoException("El UniCambista no está registrado"));
        Oferta oferta = publicacion.recibirOferta(oferente, request.montoPropuesto());
        publicacionRepository.guardar(publicacion);
        return OfertaResponse.desde(oferta);
    }
}
