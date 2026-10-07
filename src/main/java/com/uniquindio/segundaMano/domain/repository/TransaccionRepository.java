package com.uniquindio.segundaMano.domain.repository;

import com.uniquindio.segundaMano.domain.entity.Transaccion;
import com.uniquindio.segundaMano.domain.entity.UniCambista;

import java.util.List;
import java.util.Optional;

public interface TransaccionRepository {

    Optional<Transaccion> buscarPorId(String id);

    void guardar(Transaccion transaccion);

    List<Transaccion> listarPorParticipante(UniCambista uniCambista);
}
