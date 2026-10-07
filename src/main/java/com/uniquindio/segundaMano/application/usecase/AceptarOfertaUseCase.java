package com.uniquindio.segundaMano.application.usecase;

import com.uniquindio.segundaMano.domain.entity.Oferta;
import com.uniquindio.segundaMano.domain.entity.Publicacion;
import com.uniquindio.segundaMano.domain.entity.Transaccion;
import com.uniquindio.segundaMano.domain.entity.UniCambista;
import com.uniquindio.segundaMano.domain.exception.ReglaDominioException;
import com.uniquindio.segundaMano.domain.repository.PublicacionRepository;
import com.uniquindio.segundaMano.domain.repository.TransaccionRepository;

public class AceptarOfertaUseCase {

    private final PublicacionRepository publicacionRepository;
    private final TransaccionRepository transaccionRepository;

    public AceptarOfertaUseCase(PublicacionRepository publicacionRepository,TransaccionRepository transaccionRepository) {
        this.publicacionRepository = publicacionRepository;
        this.transaccionRepository = transaccionRepository;
    }

    public Transaccion ejecutar(String codigoPublicacion, String idOferta, UniCambista dueno) {
        Publicacion publicacion = publicacionRepository.buscarPorCodigo(codigoPublicacion).orElseThrow(() -> new ReglaDominioException("La Publicación no existe"));
        publicacion.aceptarOferta(idOferta, dueno);
        Oferta ofertaAceptada = publicacion.getOfertas().stream().filter(o -> o.getId().equals(idOferta)).findFirst().orElseThrow(() -> new ReglaDominioException("La Oferta no pertenece a esta Publicación"));
        Transaccion transaccion = Transaccion.iniciar(ofertaAceptada);
        publicacionRepository.guardar(publicacion);
        transaccionRepository.guardar(transaccion);
        return transaccion;
    }
}
