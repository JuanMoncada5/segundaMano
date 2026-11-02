package com.uniquindio.segundaMano.application.usecase;

import com.uniquindio.segundaMano.domain.entity.Publicacion;
import com.uniquindio.segundaMano.domain.entity.UniCambista;
import com.uniquindio.segundaMano.domain.exception.RecursoNoEncontradoException;
import com.uniquindio.segundaMano.domain.repository.PublicacionRepository;
import com.uniquindio.segundaMano.domain.repository.UniCambistaRepository;

/**
 * Eliminación LÓGICA: la fila no se borra, la Publicación pasa a estado ELIMINADA.
 * La regla "solo el dueño puede eliminar" vive en Publicacion.eliminarLogicamente (dominio), no aquí.
 */
public class EliminarPublicacionUseCase {

    private final PublicacionRepository publicacionRepository;
    private final UniCambistaRepository uniCambistaRepository;

    public EliminarPublicacionUseCase(PublicacionRepository publicacionRepository,
                                      UniCambistaRepository uniCambistaRepository) {
        this.publicacionRepository = publicacionRepository;
        this.uniCambistaRepository = uniCambistaRepository;
    }

    /** @param correoSolicitante sale del token (quién está autenticado), nunca del body */
    public void ejecutar(String codigoPublicacion, String correoSolicitante) {
        Publicacion publicacion = publicacionRepository.buscarPorCodigo(codigoPublicacion)
                .orElseThrow(() -> new RecursoNoEncontradoException("La Publicación no existe"));
        UniCambista solicitante = uniCambistaRepository.buscarPorCorreo(correoSolicitante)
                .orElseThrow(() -> new RecursoNoEncontradoException("El UniCambista no está registrado"));
        publicacion.eliminarLogicamente(solicitante);
        publicacionRepository.guardar(publicacion);
    }
}