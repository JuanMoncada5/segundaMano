package com.uniquindio.segundaMano.support;

import com.uniquindio.segundaMano.application.auth.Credencial;
import com.uniquindio.segundaMano.application.auth.CredencialRepository;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

/** Repositorio de credenciales en memoria, solo para pruebas unitarias. */
public class CredencialRepositoryEnMemoria implements CredencialRepository {

    private final Map<String, Credencial> almacen = new HashMap<>();

    @Override
    public Optional<Credencial> buscarPorCorreo(String correoInstitucional) {
        return Optional.ofNullable(almacen.get(correoInstitucional));
    }

    @Override
    public void guardar(Credencial credencial) {
        almacen.put(credencial.correoInstitucional(), credencial);
    }

    public int cantidad() {
        return almacen.size();
    }
}
