package com.uniquindio.segundaMano.application.usecase;

import com.uniquindio.segundaMano.application.dto.AceptarOfertaRequest;
import com.uniquindio.segundaMano.application.dto.TransaccionResponse;
import com.uniquindio.segundaMano.domain.entity.Oferta;
import com.uniquindio.segundaMano.domain.entity.Publicacion;
import com.uniquindio.segundaMano.domain.entity.Transaccion;
import com.uniquindio.segundaMano.domain.entity.UniCambista;
import com.uniquindio.segundaMano.domain.exception.ReglaDominioException;
import com.uniquindio.segundaMano.domain.exception.RecursoNoEncontradoException;
import com.uniquindio.segundaMano.domain.repository.PublicacionRepository;
import com.uniquindio.segundaMano.domain.repository.TransaccionRepository;
import com.uniquindio.segundaMano.domain.repository.UniCambistaRepository;

public class AceptarOfertaUseCase {

    private final PublicacionRepository publicacionRepository;
    private final TransaccionRepository transaccionRepository;
    private final UniCambistaRepository uniCambistaRepository;

    public AceptarOfertaUseCase(PublicacionRepository publicacionRepository,
                                TransaccionRepository transaccionRepository,
                                UniCambistaRepository uniCambistaRepository) {
        this.publicacionRepository = publicacionRepository;
        this.transaccionRepository = transaccionRepository;
        this.uniCambistaRepository = uniCambistaRepository;
    }

    public TransaccionResponse ejecutar(AceptarOfertaRequest request) {
        Publicacion publicacion = publicacionRepository.buscarPorCodigo(request.codigoPublicacion())
                .orElseThrow(() -> new RecursoNoEncontradoException("La Publicación no existe"));
        UniCambista dueno = uniCambistaRepository.buscarPorCorreo(request.correoDueno())
                .orElseThrow(() -> new RecursoNoEncontradoException("El UniCambista no está registrado"));

        publicacion.aceptarOferta(request.idOferta(), dueno);
        Oferta ofertaAceptada = publicacion.getOfertas().stream()
                .filter(o -> o.getId().equals(request.idOferta()))
                .findFirst()
                .orElseThrow(() -> new ReglaDominioException("La Oferta no pertenece a esta Publicación"));
        Transaccion transaccion = Transaccion.iniciar(ofertaAceptada);

        publicacionRepository.guardar(publicacion);
        transaccionRepository.guardar(transaccion);
        return TransaccionResponse.desde(transaccion);
    }
}
