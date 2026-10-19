package com.uniquindio.segundaMano.domain.repository;

import com.uniquindio.segundaMano.domain.entity.UniCambista;

import java.util.Optional;

public interface UniCambistaRepository {
    Optional<UniCambista> buscarPorCorreo(String correoInstitucional);

    void guardar(UniCambista uniCambista);
}
