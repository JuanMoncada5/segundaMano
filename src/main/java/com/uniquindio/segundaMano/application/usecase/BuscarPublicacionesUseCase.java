package com.uniquindio.segundaMano.application.usecase;

import com.uniquindio.segundaMano.application.dto.BuscarPublicacionesRequest;
import com.uniquindio.segundaMano.application.dto.PaginaResponse;
import com.uniquindio.segundaMano.application.dto.PublicacionResponse;
import com.uniquindio.segundaMano.domain.entity.Publicacion;
import com.uniquindio.segundaMano.domain.repository.CriteriosBusquedaPublicacion;
import com.uniquindio.segundaMano.domain.repository.Pagina;
import com.uniquindio.segundaMano.domain.repository.PublicacionRepository;

/**
 * Búsqueda paginada de Publicaciones activas.
 * Aquí solo se validan las ENTRADAS (página, tamaño, rango): eso no es una regla de negocio,
 * por eso lanza IllegalArgumentException (400) y no ReglaDominioException (422).
 */
public class BuscarPublicacionesUseCase {

    public static final int TAMANO_MAXIMO = 50;

    private final PublicacionRepository publicacionRepository;

    public BuscarPublicacionesUseCase(PublicacionRepository publicacionRepository) {
        this.publicacionRepository = publicacionRepository;
    }

    public PaginaResponse<PublicacionResponse> ejecutar(BuscarPublicacionesRequest request) {
        if (request.pagina() < 0) {
            throw new IllegalArgumentException("La página no puede ser negativa");
        }
        if (request.tamano() < 1 || request.tamano() > TAMANO_MAXIMO) {
            throw new IllegalArgumentException("El tamaño de página debe estar entre 1 y " + TAMANO_MAXIMO);
        }
        if (request.valorMin() != null && request.valorMax() != null
                && request.valorMin().compareTo(request.valorMax()) > 0) {
            throw new IllegalArgumentException("valorMin no puede ser mayor que valorMax");
        }

        CriteriosBusquedaPublicacion criterios = new CriteriosBusquedaPublicacion(
                vacioANull(request.texto()), vacioANull(request.curso()),
                request.valorMin(), request.valorMax(), request.pagina(), request.tamano());
        Pagina<Publicacion> pagina = publicacionRepository.buscar(criterios);

        return new PaginaResponse<>(
                pagina.contenido().stream().map(PublicacionResponse::desde).toList(),
                pagina.totalElementos(), pagina.totalPaginas(), pagina.pagina(), pagina.tamano());
    }

    private static String vacioANull(String valor) {
        return (valor == null || valor.isBlank()) ? null : valor.trim();
    }
}