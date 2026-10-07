package com.uniquindio.segundaMano.infraestructure.persistence;

import com.uniquindio.segundaMano.domain.entity.Transaccion;
import com.uniquindio.segundaMano.domain.entity.UniCambista;
import com.uniquindio.segundaMano.domain.repository.TransaccionRepository;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

public class TransaccionRepositoryEnMemoria implements TransaccionRepository {

    private final Map<String, Transaccion> almacen = new HashMap<>();

    @Override
    public Optional<Transaccion> buscarPorId(String id) {
        return Optional.ofNullable(almacen.get(id));
    }

    @Override
    public void guardar(Transaccion transaccion) {
        almacen.put(transaccion.getId(), transaccion);
    }

    @Override
    public List<Transaccion> listarPorParticipante(UniCambista uniCambista) {
        return almacen.values().stream()
                .filter(t -> t.getComprador().equals(uniCambista) || t.getVendedor().equals(uniCambista))
                .collect(Collectors.toList());
    }
}
