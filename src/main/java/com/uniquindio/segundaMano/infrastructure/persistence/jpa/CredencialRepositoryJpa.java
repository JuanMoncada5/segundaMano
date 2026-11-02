package com.uniquindio.segundaMano.infrastructure.persistence.jpa;

import com.uniquindio.segundaMano.application.auth.Credencial;
import com.uniquindio.segundaMano.application.auth.CredencialRepository;
import com.uniquindio.segundaMano.infrastructure.persistence.jpa.entity.CredencialEntity;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public class CredencialRepositoryJpa implements CredencialRepository {

    private final CredencialJpaDataRepository jpaRepository;

    public CredencialRepositoryJpa(CredencialJpaDataRepository jpaRepository) {
        this.jpaRepository = jpaRepository;
    }

    @Override
    public Optional<Credencial> buscarPorCorreo(String correoInstitucional) {
        return jpaRepository.findById(correoInstitucional)
                .map(e -> new Credencial(e.getCorreoInstitucional(), e.getContrasenaHash(), e.getRol()));
    }

    @Override
    public void guardar(Credencial credencial) {
        jpaRepository.save(new CredencialEntity(
                credencial.correoInstitucional(), credencial.contrasenaHash(), credencial.rol()));
    }
}
