package com.uniquindio.segundaMano.infrastructure.persistence;

import com.uniquindio.segundaMano.domain.entity.UniCambista;
import com.uniquindio.segundaMano.domain.repository.UniCambistaRepository;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class UniCambistaRepositoryEnMemoria implements UniCambistaRepository {

    private final Map<String, UniCambista> almacen = new HashMap<>();

    @Override
    public Optional<UniCambista> buscarPorCorreo(String correoInstitucional) {
        if (correoInstitucional == null) {
            return Optional.empty();
        }
        return Optional.ofNullable(almacen.get(UniCambista.normalizarCorreo(correoInstitucional)));
    }

    @Override
    public void guardar(UniCambista uniCambista) {
        almacen.put(uniCambista.getCorreoInstitucional(), uniCambista);
    }
}