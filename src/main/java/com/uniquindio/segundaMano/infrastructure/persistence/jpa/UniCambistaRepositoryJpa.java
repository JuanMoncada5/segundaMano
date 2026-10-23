package com.uniquindio.segundaMano.infrastructure.persistence.jpa;

import com.uniquindio.segundaMano.domain.entity.UniCambista;
import com.uniquindio.segundaMano.domain.repository.UniCambistaRepository;
import com.uniquindio.segundaMano.infrastructure.persistence.jpa.mapper.UniCambistaPersistenceMapper;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public class UniCambistaRepositoryJpa implements UniCambistaRepository {

    private final UniCambistaJpaDataRepository jpaRepository;
    private final UniCambistaPersistenceMapper mapper;

    public UniCambistaRepositoryJpa(UniCambistaJpaDataRepository jpaRepository,
                                    UniCambistaPersistenceMapper mapper) {
        this.jpaRepository = jpaRepository;
        this.mapper = mapper;
    }

    @Override
    public Optional<UniCambista> buscarPorCorreo(String correoInstitucional) {
        if (correoInstitucional == null) {
            return Optional.empty();
        }
        return jpaRepository.findById(UniCambista.normalizarCorreo(correoInstitucional))
                .map(mapper::toDomain);
    }

    @Override
    public void guardar(UniCambista uniCambista) {
        jpaRepository.save(mapper.toEntity(uniCambista));
    }
}
