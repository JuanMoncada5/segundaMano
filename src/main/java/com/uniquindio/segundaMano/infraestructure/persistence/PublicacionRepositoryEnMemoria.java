package com.uniquindio.segundaMano.infraestructure.persistence;

import com.uniquindio.segundaMano.domain.entity.Publicacion;
import com.uniquindio.segundaMano.domain.repository.PublicacionRepository;
import com.uniquindio.segundaMano.domain.valueObject.EstadoPublicacion;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
public class PublicacionRepositoryEnMemoria implements PublicacionRepository {

    private final Map<String, Publicacion> almacen = new HashMap<>();

    @Override
    public Optional<Publicacion> buscarPorCodigo(String codigo) {
        return Optional.ofNullable(almacen.get(codigo));
    }

    @Override
    public void guardar(Publicacion publicacion) {
        almacen.put(publicacion.getCodigo(), publicacion);
    }

    @Override
    public List<Publicacion> listarActivas() {
        return almacen.values().stream()
                .filter(p -> p.getEstado() == EstadoPublicacion.ACTIVA)
                .collect(Collectors.toList());
    }
}