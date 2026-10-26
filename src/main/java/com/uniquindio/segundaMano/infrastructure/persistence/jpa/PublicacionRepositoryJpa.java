package com.uniquindio.segundaMano.infrastructure.persistence.jpa;

import com.uniquindio.segundaMano.domain.entity.Publicacion;
import com.uniquindio.segundaMano.domain.entity.UniCambista;
import com.uniquindio.segundaMano.domain.repository.PublicacionRepository;
import com.uniquindio.segundaMano.domain.repository.UniCambistaRepository;
import com.uniquindio.segundaMano.domain.valueObject.EstadoPublicacion;
import com.uniquindio.segundaMano.infrastructure.persistence.jpa.entity.PublicacionEntity;
import com.uniquindio.segundaMano.infrastructure.persistence.jpa.mapper.PublicacionPersistenceMapper;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.data.domain.PageRequest;

import java.util.List;
import java.util.Optional;

@Repository
public class PublicacionRepositoryJpa implements PublicacionRepository {

    private final PublicacionJpaDataRepository publicacionJpa;
    private final OfertaJpaDataRepository ofertaJpa;
    private final UniCambistaRepository uniCambistaRepository;
    private final PublicacionPersistenceMapper mapper;

    public PublicacionRepositoryJpa(PublicacionJpaDataRepository publicacionJpa,
                                    OfertaJpaDataRepository ofertaJpa,
                                    UniCambistaRepository uniCambistaRepository,
                                    PublicacionPersistenceMapper mapper) {
        this.publicacionJpa = publicacionJpa;
        this.ofertaJpa = ofertaJpa;
        this.uniCambistaRepository = uniCambistaRepository;
        this.mapper = mapper;
    }

    @Override
    public Optional<Publicacion> buscarPorCodigo(String codigo) {
        return publicacionJpa.findById(codigo).map(this::aDominio);
    }

    /** La Publicación y sus Ofertas se guardan juntas: son un solo Agregado. */
    @Override
    @Transactional
    public void guardar(Publicacion publicacion) {
        publicacionJpa.save(mapper.toEntity(publicacion));
        ofertaJpa.saveAll(mapper.toOfertaEntities(publicacion));
    }

    @Override
    public List<Publicacion> listarActivas() {
        return publicacionJpa.findByEstado(EstadoPublicacion.ACTIVA).stream()
                .map(this::aDominio)
                .toList();
    }

    @Override
    public List<Publicacion> listarMasOfertadas(int limite) {
        return publicacionJpa.rankingCodigosPorCantidadDeOfertas(EstadoPublicacion.ACTIVA, PageRequest.of(0, limite))
                .stream()
                .map(codigo -> publicacionJpa.findById(codigo).orElseThrow())
                .map(this::aDominio)
                .toList();
    }

    private Publicacion aDominio(PublicacionEntity entity) {
        UniCambista dueno = buscarUniCambista(entity.getDuenoCorreo());
        List<Publicacion.DatosOferta> ofertas = ofertaJpa.findByPublicacionCodigo(entity.getCodigo()).stream()
                .map(oferta -> mapper.toDatosOferta(oferta, buscarUniCambista(oferta.getOferenteCorreo())))
                .toList();
        return mapper.toDomain(entity, dueno, ofertas);
    }

    private UniCambista buscarUniCambista(String correo) {
        return uniCambistaRepository.buscarPorCorreo(correo)
                .orElseThrow(() -> new IllegalStateException(
                        "Dato inconsistente en la base de datos: no existe el UniCambista " + correo));
    }
}
