package com.uniquindio.segundaMano.application.auth;

import java.util.Optional;

public interface CredencialRepository {

    Optional<Credencial> buscarPorCorreo(String correoInstitucional);

    void guardar(Credencial credencial);
}
