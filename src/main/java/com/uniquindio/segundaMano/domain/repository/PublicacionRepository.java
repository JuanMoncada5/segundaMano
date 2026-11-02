package com.uniquindio.segundaMano.domain.repository;

import com.uniquindio.segundaMano.domain.entity.Publicacion;

import java.util.List;
import java.util.Optional;

public interface PublicacionRepository {

    Optional<Publicacion> buscarPorCodigo(String codigo);

    void guardar(Publicacion publicacion);

    List<Publicacion> listarActivas();

    List<Publicacion> listarMasOfertadas(int limite);

    /**
     * Búsqueda paginada: solo Publicaciones ACTIVAS (nunca las eliminadas), filtradas por
     * texto en el título, curso y rango de valor. El filtrado y el conteo los hace la base de datos.
     */
    Pagina<Publicacion> buscar(CriteriosBusquedaPublicacion criterios);
}