package com.uniquindio.segundaMano.application.usecase;

import com.uniquindio.segundaMano.domain.entity.Oferta;
import com.uniquindio.segundaMano.domain.entity.Publicacion;
import com.uniquindio.segundaMano.domain.entity.UniCambista;
import com.uniquindio.segundaMano.domain.exception.ReglaDominioException;
import com.uniquindio.segundaMano.domain.repository.PublicacionRepository;

import java.math.BigDecimal;

public class OfertarArticuloUseCase {
    private final PublicacionRepository publicacionRepository;

    public OfertarArticuloUseCase(PublicacionRepository publicacionRepository) {
        this.publicacionRepository = publicacionRepository;
    }

    public Oferta ejecutar(String codigoPublicacion, UniCambista oferente, BigDecimal monto) {
        Publicacion publicacion = publicacionRepository.buscarPorCodigo(codigoPublicacion).orElseThrow(() -> new ReglaDominioException("La Publicación no existe"));
        Oferta oferta = publicacion.recibirOferta(oferente, monto);
        publicacionRepository.guardar(publicacion);
        return oferta;
    }
}
