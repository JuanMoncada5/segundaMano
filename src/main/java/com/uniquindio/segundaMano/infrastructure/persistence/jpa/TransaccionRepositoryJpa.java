package com.uniquindio.segundaMano.infrastructure.persistence.jpa;

import com.uniquindio.segundaMano.domain.entity.Publicacion;
import com.uniquindio.segundaMano.domain.entity.Transaccion;
import com.uniquindio.segundaMano.domain.entity.UniCambista;
import com.uniquindio.segundaMano.domain.repository.PublicacionRepository;
import com.uniquindio.segundaMano.domain.repository.TransaccionRepository;
import com.uniquindio.segundaMano.domain.repository.UniCambistaRepository;
import com.uniquindio.segundaMano.infrastructure.persistence.jpa.entity.TransaccionEntity;
import com.uniquindio.segundaMano.infrastructure.persistence.jpa.mapper.TransaccionPersistenceMapper;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public class TransaccionRepositoryJpa implements TransaccionRepository {

    private final TransaccionJpaDataRepository jpaRepository;
    private final PublicacionRepository publicacionRepository;
    private final UniCambistaRepository uniCambistaRepository;
    private final TransaccionPersistenceMapper mapper;

    public TransaccionRepositoryJpa(TransaccionJpaDataRepository jpaRepository,
                                    PublicacionRepository publicacionRepository,
                                    UniCambistaRepository uniCambistaRepository,
                                    TransaccionPersistenceMapper mapper) {
        this.jpaRepository = jpaRepository;
        this.publicacionRepository = publicacionRepository;
        this.uniCambistaRepository = uniCambistaRepository;
        this.mapper = mapper;
    }

    @Override
    public Optional<Transaccion> buscarPorId(String id) {
        return jpaRepository.findById(id).map(this::aDominio);
    }

    @Override
    public void guardar(Transaccion transaccion) {
        jpaRepository.save(mapper.toEntity(transaccion));
    }

    @Override
    public List<Transaccion> listarPorParticipante(UniCambista uniCambista) {
        String correo = uniCambista.getCorreoInstitucional();
        return jpaRepository.findByCompradorCorreoOrVendedorCorreo(correo, correo).stream()
                .map(this::aDominio)
                .toList();
    }

    private Transaccion aDominio(TransaccionEntity entity) {
        Publicacion publicacion = publicacionRepository.buscarPorCodigo(entity.getPublicacionCodigo())
                .orElseThrow(() -> new IllegalStateException(
                        "Dato inconsistente en la base de datos: no existe la Publicación " + entity.getPublicacionCodigo()));
        return mapper.toDomain(entity, publicacion,
                buscarUniCambista(entity.getCompradorCorreo()),
                buscarUniCambista(entity.getVendedorCorreo()));
    }

    private UniCambista buscarUniCambista(String correo) {
        return uniCambistaRepository.buscarPorCorreo(correo)
                .orElseThrow(() -> new IllegalStateException(
                        "Dato inconsistente en la base de datos: no existe el UniCambista " + correo));
    }
}
