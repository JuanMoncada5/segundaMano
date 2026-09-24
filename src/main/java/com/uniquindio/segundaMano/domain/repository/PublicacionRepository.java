package com.uniquindio.segundaMano.domain.repository;

import com.uniquindio.segundaMano.domain.entity.Publicacion;

import java.util.List;
import java.util.Optional;

public interface PublicacionRepository {

    Optional<Publicacion> buscarPorCodigo(String codigo);

    void guardar(Publicacion publicacion);

    List<Publicacion> listarActivas();
}