package com.uniquindio.segundaMano.infrastructure.persistence;

import com.uniquindio.segundaMano.domain.entity.Publicacion;
import com.uniquindio.segundaMano.domain.repository.CriteriosBusquedaPublicacion;
import com.uniquindio.segundaMano.domain.repository.Pagina;
import com.uniquindio.segundaMano.domain.repository.PublicacionRepository;
import com.uniquindio.segundaMano.domain.valueObject.EstadoPublicacion;

import java.util.Comparator;
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

    @Override
    public List<Publicacion> listarMasOfertadas(int limite) {
        return almacen.values().stream()
                .filter(p -> p.getEstado() == EstadoPublicacion.ACTIVA && !p.getOfertas().isEmpty())
                .sorted(Comparator.comparingInt((Publicacion p) -> p.getOfertas().size()).reversed()
                        .thenComparing(Publicacion::getCodigo))
                .limit(limite)
                .collect(Collectors.toList());
    }

    /** Misma búsqueda que la versión JPA, pero en Java (solo se usa en pruebas). */
    @Override
    public Pagina<Publicacion> buscar(CriteriosBusquedaPublicacion c) {
        List<Publicacion> filtradas = almacen.values().stream()
                .filter(p -> p.getEstado() == EstadoPublicacion.ACTIVA)
                .filter(p -> c.texto() == null || p.getTitulo().toLowerCase().contains(c.texto().toLowerCase()))
                .filter(p -> c.curso() == null || (p.getVigenciaAcademica() != null
                        && p.getVigenciaAcademica().curso().equalsIgnoreCase(c.curso())))
                .filter(p -> c.valorMin() == null || p.getValor().compareTo(c.valorMin()) >= 0)
                .filter(p -> c.valorMax() == null || p.getValor().compareTo(c.valorMax()) <= 0)
                .sorted(Comparator.comparing(Publicacion::getValor).thenComparing(Publicacion::getCodigo))
                .collect(Collectors.toList());
        int desde = Math.min(c.pagina() * c.tamano(), filtradas.size());
        int hasta = Math.min(desde + c.tamano(), filtradas.size());
        int totalPaginas = (int) Math.ceil((double) filtradas.size() / c.tamano());
        return new Pagina<>(filtradas.subList(desde, hasta), filtradas.size(), totalPaginas, c.pagina(), c.tamano());
    }
}